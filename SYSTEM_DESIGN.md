# TFT-Nexus-AI System Design

## 1. 시스템 개요

TFT-Nexus-AI는 Riot Games의 TFT(Teamfight Tactics) 플레이어들이 자신의 게임 전적을 AI 기반으로 분석하여 플레이 스타일 개선 통찰을 얻을 수 있는 웹 애플리케이션입니다.

### 핵심 기능
- **사용자 인증**: Riot Games OAuth 2.0 연동
- **전적 수집**: Riot API를 통한 TFT 매치 데이터 수집
- **AI 분석**: 외부 AI API를 활용한 플레이 스타일 분석
- **결과 시각화**: 분석 결과를 사용자 친화적으로 표시

## 2. 시스템 아키텍처

### 2.1 모듈러 모노리스 구조

```
TFT-Nexus-AI/
├── app/                    # Presentation Layer
│   ├── auth/              # 인증 컨트롤러
│   ├── match/             # 전적 컨트롤러  
│   └── analysis/          # 분석 컨트롤러
├── domain/                 # Business Layer
│   ├── user/              # 사용자 도메인
│   ├── match/             # 전적 도메인
│   └── analysis/          # 분석 도메인
├── storage/                # Data Access Layer
│   ├── user/              # 사용자 영속성
│   ├── match/             # 전적 영속성
│   └── analysis/          # 분석 영속성
└── clients/                # External Integration
    ├── riot-api/          # Riot API 클라이언트
    └── ai-client/         # AI API 클라이언트
```

### 2.2 레이어 구조 (토스 아키텍처 원칙 적용)

#### Presentation Layer (app 모듈)
- **책임**: 외부 요청/응답 처리, API 엔드포인트
- **특징**: 외부 변화에 민감, HTTP/JSON 처리

#### Business Layer (domain 모듈)
- **책임**: 비즈니스 로직, 도메인 규칙
- **특징**: 비즈니스 흐름 중심, 상세 구현 숨김

#### Implement Layer (domain 내 서비스 클래스들)
- **책임**: 상세 구현 로직, 도구 역할
- **특징**: 재사용 가능한 구현체들

#### Data Access Layer (storage + clients 모듈)
- **책임**: 데이터 영속성, 외부 시스템 연동
- **특징**: 기술 의존성 격리

### 2.3 레이어 제약 규칙

1. **순방향 참조**: 상위 레이어만 하위 레이어 참조
2. **역류 방지**: 하위 레이어는 상위 레이어 참조 금지
3. **레이어 건너뛰기 방지**: 인접한 레이어만 참조
4. **동일 레이어 참조 제한**: Implement Layer만 예외

## 3. Riot API 통합 설계

### 3.1 API 클라이언트 구조

```java
// clients/riot-api 모듈
public interface RiotAccountClient {
    AccountDto getAccountByRiotId(String gameName, String tagLine);
}

public interface TftMatchClient {
    List<String> getMatchIdsByPuuid(String puuid, int count);
    MatchDto getMatchById(String matchId);
}

public interface TftSummonerClient {
    SummonerDto getSummonerByPuuid(String puuid);
}
```

### 3.2 데이터 플로우

```
사용자 로그인 → Riot OAuth → PUUID 획득
       ↓
PUUID → TFT Match API → Match ID 목록
       ↓
Match ID → TFT Match API → 상세 매치 데이터
       ↓
매치 데이터 → AI API → 분석 결과
```

## 4. 데이터베이스 설계 (DDD Aggregate)

### 4.1 User Aggregate

```sql
-- 사용자 루트 엔티티
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    puuid VARCHAR(78) UNIQUE NOT NULL,
    game_name VARCHAR(255) NOT NULL,
    tag_line VARCHAR(255) NOT NULL,
    summoner_level BIGINT,
    profile_icon_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 4.2 Match Aggregate

```sql
-- 매치 루트 엔티티
CREATE TABLE matches (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    match_id VARCHAR(255) UNIQUE NOT NULL,
    game_version VARCHAR(50),
    game_length FLOAT,
    game_creation BIGINT,
    tft_set_number INT,
    tft_set_core_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 참여자 정보
CREATE TABLE participants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    match_id BIGINT REFERENCES matches(id),
    puuid VARCHAR(78) NOT NULL,
    placement INT NOT NULL,
    level INT,
    last_round INT,
    gold_left INT,
    total_damage_to_players INT,
    players_eliminated INT,
    time_eliminated FLOAT
);

-- 특성 정보
CREATE TABLE participant_traits (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    participant_id BIGINT REFERENCES participants(id),
    name VARCHAR(100) NOT NULL,
    num_units INT,
    style INT,
    tier_current INT,
    tier_total INT
);

-- 유닛 정보
CREATE TABLE participant_units (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    participant_id BIGINT REFERENCES participants(id),
    character_id VARCHAR(100) NOT NULL,
    name VARCHAR(100),
    rarity INT,
    tier INT
);

-- 아이템 정보
CREATE TABLE participant_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    unit_id BIGINT REFERENCES participant_units(id),
    item_id INT NOT NULL,
    item_name VARCHAR(100)
);
```

### 4.3 Analysis Aggregate

```sql
-- 분석 루트 엔티티
CREATE TABLE analyses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT REFERENCES users(id),
    analysis_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'PROCESSING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL
);

-- 분석 결과
CREATE TABLE analysis_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    analysis_id BIGINT REFERENCES analyses(id),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    insight_type VARCHAR(50),
    confidence_score DECIMAL(3,2)
);

-- 분석에 사용된 매치들
CREATE TABLE analysis_matches (
    analysis_id BIGINT REFERENCES analyses(id),
    match_id BIGINT REFERENCES matches(id),
    PRIMARY KEY (analysis_id, match_id)
);
```

## 5. REST API 설계

### 5.1 인증 API

```yaml
# Riot OAuth 로그인
POST /api/auth/riot
Request Body:
  {
    "authCode": "string"
  }
Response:
  {
    "accessToken": "string",
    "user": {
      "puuid": "string",
      "gameName": "string",
      "tagLine": "string"
    }
  }

# 사용자 프로필 조회
GET /api/auth/profile
Response:
  {
    "puuid": "string",
    "gameName": "string", 
    "tagLine": "string",
    "summonerLevel": 200,
    "profileIconId": 1234
  }
```

### 5.2 전적 API

```yaml
# 사용자 전적 목록 조회
GET /api/matches?page=0&size=20
Response:
  {
    "content": [
      {
        "matchId": "string",
        "gameVersion": "string",
        "gameLength": 1234.5,
        "placement": 3,
        "gameCreation": 1640995200000
      }
    ],
    "totalElements": 100,
    "totalPages": 5
  }

# 특정 경기 상세 조회
GET /api/matches/{matchId}
Response:
  {
    "matchId": "string",
    "gameInfo": {
      "gameVersion": "string",
      "gameLength": 1234.5,
      "tftSetNumber": 12
    },
    "participant": {
      "placement": 3,
      "level": 8,
      "goldLeft": 5,
      "traits": [...],
      "units": [...]
    }
  }

# 최신 전적 동기화
POST /api/matches/sync
Response:
  {
    "syncedCount": 15,
    "message": "Successfully synced 15 new matches"
  }
```

### 5.3 분석 API

```yaml
# AI 분석 요청
POST /api/analysis/request
Request Body:
  {
    "analysisType": "PLAY_STYLE",
    "matchCount": 20
  }
Response:
  {
    "analysisId": "string",
    "status": "PROCESSING",
    "estimatedCompletionTime": "2024-01-15T10:30:00Z"
  }

# 분석 결과 조회
GET /api/analysis/{analysisId}
Response:
  {
    "id": "string",
    "status": "COMPLETED",
    "results": [
      {
        "title": "플레이 스타일 분석",
        "content": "당신은 경제 운영에 강점을 보입니다...",
        "insightType": "STRENGTH",
        "confidenceScore": 0.85
      }
    ],
    "completedAt": "2024-01-15T10:35:00Z"
  }
```

## 6. 기술 스택

### 6.1 백엔드 기술
- **언어**: Java 21
- **프레임워크**: Spring Boot 3.x
- **빌드 도구**: Gradle
- **데이터베이스**: MySQL 8.0
- **ORM**: JPA/Hibernate
- **HTTP 클라이언트**: WebClient (Spring WebFlux)

### 6.2 외부 연동
- **Riot API**: 공식 REST API
- **AI API**: Claude/GPT API (단기), 자체 모델 (장기)
- **인증**: Riot Games OAuth 2.0

## 7. 개발 전략

### 7.1 단계별 개발 계획

**Phase 1: 기반 구조**
- 모듈 구조 설정
- 기본 도메인 모델 구현
- Riot API 클라이언트 구현

**Phase 2: 핵심 기능**
- 사용자 인증 구현
- 전적 수집 및 저장
- 기본 API 엔드포인트

**Phase 3: AI 통합**
- 외부 AI API 연동
- 분석 요청/결과 처리
- 분석 결과 시각화

**Phase 4: 고도화**
- 성능 최적화
- 사용자 경험 개선
- 자체 ML 모델 검토

### 7.2 품질 보증

- **TDD 적용**: 모든 기능에 대한 테스트 우선 작성
- **레이어 테스트**: 단위 테스트, 통합 테스트, E2E 테스트
- **API 문서화**: OpenAPI/Swagger 자동 생성

## 8. 확장성 고려사항

### 8.1 성능 최적화
- **캐싱**: Redis를 활용한 Riot API 응답 캐싱
- **배치 처리**: 대량 전적 수집을 위한 Spring Batch
- **비동기 처리**: AI 분석 요청의 비동기 처리

### 8.2 모듈 확장
- **모듈 분리**: 필요시 clients 모듈을 세분화
- **기술 교체**: implementation 키워드로 기술 의존성 격리
- **마이크로서비스**: 향후 필요시 모듈별 분리 가능

이 설계는 토스 블로그의 지속 성장 가능한 소프트웨어 원칙을 적용하여 **통제 가능하고 제어 가능한** 시스템을 목표로 합니다.