# TFT-Nexus-AI 리팩토링 분석 리포트

> 토스 블로그의 "지속 성장 가능한 소프트웨어" 원칙 적용을 위한 현재 코드 문제점 분석

## 🚨 심각한 문제점들

### 1. 컴파일 에러 (긴급 수정 필요)

#### MatchService.java 라인 71-72
```java
// ❌ 현재 코드 - userService 필드가 선언되지 않았음
return userService.findByPuuid(account.puuid())
    .orElseGet(() -> userService.registerUser(account.puuid(), account.gameName(), account.tagLine()));
```

#### RiotLoginController.java 라인 16
```java
// ❌ 현재 코드 - OAuth2LoginUseCase 클래스가 존재하지 않음
private final OAuth2LoginUseCase oAuth2LoginUseCase;
```

### 2. 심각한 레이어 아키텍처 위반

현재 코드는 토스 블로그에서 경고하는 **모든 레이어 제약 규칙을 위반**하고 있습니다.

#### 🔥 레이어 역류 위반 (2번째 규칙)
```java
// ❌ domain/match/MatchService.java
package org.project.domain.match;

import org.project.app.client.RiotApiClient;        // domain → app 역류!
import org.project.app.exception.RiotApiException;   // domain → app 역류!
import org.project.app.exception.CollectMatchHistoryException; // domain → app 역류!
```

**문제점**: domain이 상위 레이어인 app을 알고 있음 → 토스 원칙 완전 위반

#### ⚠️ 레이어 건너뛰기 위반 (3번째 규칙)
```java
// ❌ MatchService.java - Business Layer가 Data Access Layer 직접 호출
private User findOrCreateUser(String gameName, String tagLine) {
    RiotApiClient.AccountDto account = riotApiClient.getPuuidByRiotId(gameName, tagLine);
    // Business Layer가 Implement Layer 없이 바로 외부 API 호출
}
```

**문제점**: Business Layer가 상세 구현 기술을 직접 알고 있음

## 📊 비즈니스 로직 가시성 문제 분석

### 현재 코드: 전형적인 "홍길동 코드" 패턴

```java
// ❌ 현재 MatchService.collectMatchHistory() - 상세 구현 로직 중심
@Transactional
public MatchCollectionResult collectMatchHistory(String gameName, String tagLine) {
    // 1. 입력값 검증 (상세 구현)
    validateInput(gameName, tagLine);
    
    // 2. 사용자 조회/생성 (상세 구현)
    RiotApiClient.AccountDto account = riotApiClient.getPuuidByRiotId(gameName, tagLine);
    if (account == null) {
        throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
    }
    User user = userService.findByPuuid(account.puuid())
        .orElseGet(() -> userService.registerUser(...));
    
    // 3. 신규 매치 식별 (상세 구현)
    List<String> recentMatchIds = riotApiClient.getMatchIdsByPuuid(puuid);
    List<String> existingMatchIds = matchRepository.findExistingMatchIds(recentMatchIds);
    List<String> newMatches = recentMatchIds.stream()
        .filter(id -> !existingMatchIds.contains(id))
        .collect(Collectors.toList());
    
    // 4. 매치 저장 (상세 구현)
    for (String matchId : matchIds) {
        RiotApiClient.MatchDto matchDto = riotApiClient.getMatchDetailByMatchId(matchId);
        matchRepository.save(Match.builder()...);
    }
    
    return new MatchCollectionResult(...);
}
```

**문제점**:
- 신규 개발자가 봤을 때 **상세 구현에 묻혀서 비즈니스 흐름을 파악하기 어려움**
- 토스에서 말하는 "상세 구현 로직은 잘 모르더라도 비즈니스 흐름은 이해 가능한 로직"과 정반대

### 토스 원칙 적용 시 개선된 모습

```java
// ✅ 토스 스타일 - 비즈니스 흐름 중심
@Transactional  
public MatchCollectionResult collectMatchHistory(String gameName, String tagLine) {
    // 1. 사용자 찾기 또는 생성
    User user = userFinder.findOrCreateUser(gameName, tagLine);
    
    // 2. 새로운 매치 수집
    List<Match> newMatches = matchCollector.collectNewMatches(user.getPuuid());
    
    // 3. 매치 저장
    matchStore.saveMatches(newMatches);
    
    // 4. 결과 반환
    return MatchCollectionResult.success(newMatches.size());
}
```

**개선점**:
- **비즈니스 흐름이 명확히 보임**: "사용자 찾기 → 매치 수집 → 저장 → 결과 반환"
- 상세 구현은 협력 도구 클래스들(`userFinder`, `matchCollector`, `matchStore`)이 담당
- 신규 개발자도 쉽게 이해 가능

## 🔧 단계별 리팩토링 계획

### Phase 1: 긴급 수정 (컴파일 에러 해결)

#### 1.1 MatchService의 userService 의존성 문제 해결

**방안 A: UserRepository 직접 사용**
```java
// ✅ 개선안
private User findOrCreateUser(String gameName, String tagLine) {
    RiotApiRepository riotApiRepository; // 새로 생성할 인터페이스
    AccountInfo account = riotApiRepository.getAccountByRiotId(gameName, tagLine);
    
    return userRepository.findByPuuid(account.puuid())
        .orElseGet(() -> {
            User newUser = User.create(account.puuid(), account.gameName(), account.tagLine());
            return userRepository.save(newUser);
        });
}
```

#### 1.2 OAuth2LoginUseCase 문제 해결

**방안: RiotLoginController에서 UserService 직접 사용**
```java
// ✅ 개선안
@RestController
public class RiotLoginController {
    private final RiotTokenProvider riotTokenProvider;
    private final UserService userService; // OAuth2LoginUseCase 대신 직접 사용
    
    @GetMapping("/login/oauth2/code/riot")
    public LoginResult handleRiotCallback(@RequestParam("code") String code) {
        String accessToken = riotTokenProvider.getAccessToken(code);
        return userService.login(accessToken);
    }
}
```

### Phase 2: 레이어 아키텍처 수정 (중요)

#### 2.1 domain에서 app 의존성 완전 제거

**새로운 인터페이스 생성 (domain 모듈)**
```java
// ✅ domain/riot/RiotApiRepository.java
public interface RiotApiRepository {
    AccountInfo getAccountByRiotId(String gameName, String tagLine);
    List<String> getMatchIds(String puuid);
    MatchDetail getMatchDetail(String matchId);
}

// ✅ domain 전용 DTO들
public record AccountInfo(String puuid, String gameName, String tagLine) {}
public record MatchDetail(String matchId, Long gameDatetime, Float gameLength, String gameVersion, String tftSet) {}
```

**구현체 생성 (app 모듈)**
```java
// ✅ app/riot/RiotApiRepositoryImpl.java
@Repository
public class RiotApiRepositoryImpl implements RiotApiRepository {
    private final RiotApiClient riotApiClient;
    
    @Override
    public AccountInfo getAccountByRiotId(String gameName, String tagLine) {
        RiotApiClient.AccountDto dto = riotApiClient.getPuuidByRiotId(gameName, tagLine);
        return new AccountInfo(dto.puuid(), dto.gameName(), dto.tagLine());
    }
    
    // 다른 메서드들도 동일하게 변환 로직 구현
}
```

#### 2.2 exception 패키지 정리

**domain 전용 exception 생성**
```java
// ✅ domain/exception/DomainException.java
public class MatchCollectionException extends RuntimeException {
    public MatchCollectionException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
```

### Phase 3: Implement Layer 도구 클래스 생성 (개선)

#### 3.1 협력 도구 클래스들 설계

```java
// ✅ domain/user/UserFinder.java (Implement Layer)
@Component
public class UserFinder {
    private final UserRepository userRepository;
    private final RiotApiRepository riotApiRepository;
    
    public User findOrCreateUser(String gameName, String tagLine) {
        AccountInfo account = riotApiRepository.getAccountByRiotId(gameName, tagLine);
        if (account == null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }
        
        return userRepository.findByPuuid(account.puuid())
            .orElseGet(() -> createNewUser(account));
    }
    
    private User createNewUser(AccountInfo account) {
        User newUser = User.create(account.puuid(), account.gameName(), account.tagLine());
        return userRepository.save(newUser);
    }
}

// ✅ domain/match/MatchCollector.java (Implement Layer)
@Component  
public class MatchCollector {
    private final RiotApiRepository riotApiRepository;
    private final MatchRepository matchRepository;
    
    public List<Match> collectNewMatches(String puuid) {
        List<String> recentMatchIds = riotApiRepository.getMatchIds(puuid);
        List<String> existingMatchIds = matchRepository.findExistingMatchIds(recentMatchIds);
        
        return recentMatchIds.stream()
            .filter(id -> !existingMatchIds.contains(id))
            .map(this::convertToMatch)
            .collect(Collectors.toList());
    }
    
    private Match convertToMatch(String matchId) {
        MatchDetail detail = riotApiRepository.getMatchDetail(matchId);
        return Match.create(detail.matchId(), detail.gameDatetime(), 
                          detail.gameLength(), detail.gameVersion(), detail.tftSet());
    }
}

// ✅ domain/match/MatchStore.java (Implement Layer)
@Component
public class MatchStore {
    private final MatchRepository matchRepository;
    
    public void saveMatches(List<Match> matches) {
        matches.forEach(match -> {
            try {
                matchRepository.save(match);
            } catch (Exception e) {
                log.error("매치 저장 실패: {}", match.getMatchId(), e);
            }
        });
    }
}
```

#### 3.2 최종 MatchService 리팩토링

```java
// ✅ 최종 개선된 MatchService - 토스 스타일
@Service
@RequiredArgsConstructor
public class MatchService {
    private final UserFinder userFinder;           // 협력 도구
    private final MatchCollector matchCollector;   // 협력 도구  
    private final MatchStore matchStore;           // 협력 도구
    
    @Transactional
    public MatchCollectionResult collectMatchHistory(String gameName, String tagLine) {
        // 🎯 비즈니스 흐름이 명확히 보임
        // 1. 사용자 찾기 또는 생성
        User user = userFinder.findOrCreateUser(gameName, tagLine);
        
        // 2. 새로운 매치 수집  
        List<Match> newMatches = matchCollector.collectNewMatches(user.getPuuid());
        
        // 3. 조기 반환 (비즈니스 규칙)
        if (newMatches.isEmpty()) {
            return MatchCollectionResult.noUpdate();
        }
        
        // 4. 매치 저장
        matchStore.saveMatches(newMatches);
        
        // 5. 성공 결과 반환
        return MatchCollectionResult.success(newMatches.size());
    }
}
```

## 📋 리팩토링 체크리스트

### ✅ Phase 1: 긴급 수정
- [ ] MatchService의 userService 참조 문제 해결
- [ ] OAuth2LoginUseCase 관련 컴파일 에러 수정
- [ ] 전체 프로젝트 빌드 성공 확인

### ✅ Phase 2: 레이어 아키텍처  
- [ ] domain에서 app import 문 완전 제거
- [ ] RiotApiRepository 인터페이스 생성 (domain)
- [ ] RiotApiRepositoryImpl 구현체 생성 (app) 
- [ ] domain 전용 exception 클래스 생성
- [ ] 레이어 제약 규칙 준수 확인

### ✅ Phase 3: 비즈니스 로직 개선
- [ ] UserFinder, MatchCollector, MatchStore 도구 클래스 생성
- [ ] MatchService를 비즈니스 흐름 중심으로 리팩토링
- [ ] 단위 테스트 작성 및 기존 테스트 수정
- [ ] 신규 개발자 코드 리뷰를 통한 가독성 검증

## 🎯 기대 효과

### 1. **통제 가능한 소프트웨어**
- 레이어 간 의존성이 명확해져서 변경 영향도 예측 가능
- 외부 API 변경 시 domain 로직에 영향 없음

### 2. **제어 가능한 소프트웨어**  
- 비즈니스 로직과 구현 기술의 분리
- 협력 도구 클래스들로 기능별 독립적 개발/테스트 가능

### 3. **신규 개발자 친화적**
- 비즈니스 흐름을 코드만 봐도 이해 가능
- "전적 수집이 어떻게 동작하나요?" → MatchService 코드만 보면 답변 가능

### 4. **지속 성장 가능성**
- 토스 아키텍처 원칙 준수로 기술 부채 최소화
- 새로운 요구사항 추가 시 기존 코드 영향 최소화

---

**결론**: 현재 코드는 토스 블로그에서 경고하는 "홍길동 코드"의 전형적인 사례입니다. 단계적 리팩토링을 통해 **통제와 제어가 가능한 지속 성장 가능한 소프트웨어**로 개선이 필요합니다.