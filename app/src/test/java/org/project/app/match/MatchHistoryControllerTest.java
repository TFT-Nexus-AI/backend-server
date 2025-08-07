package org.project.app.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.project.app.exception.RiotApiException;
import org.project.domain.match.Match;
import org.project.domain.match.MatchRepository;
import org.project.domain.user.User;
import org.project.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.project.app.client.RiotApiClient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * - [ ] **실패 (입력값 오류)**: `gameName` 또는 `tagLine`이 비어있는 요청 시, 400 Bad Request 오류를 반환한다.
 * - [ ] **실패 (사용자 없음)**: 존재하지 않는 라이엇 ID로 요청 시, "사용자를 찾을 수 없습니다" 오류를 반환한다.
 * - [ ] **실패 (API 통신 오류)**: 라이엇 API 통신 중 오류 발생 시, "API 통신 오류"를 반환한다.
 * - [ ] **성공 (신규 전적 없음)**: 조회된 모든 경기가 이미 DB에 존재할 경우, "새로운 전적이 없습니다" 메시지를 반환한다.
 * - [ ] **성공 (신규 사용자 & 신규 전적)**: 신규 사용자의 전적을 처음 조회하여 모두 DB에 저장하고, 저장된 경기 수를 반환한다.
 * - [ ] **성공 (기존 사용자 & 일부 신규 전적)**: 기존 사용자의 전적을 조회하여 새로운 경기만 DB에 저장하고, 새로 저장된 경기 수를 반환한다.
 */
import org.junit.jupiter.api.Tag;

@Tag("context")
@SpringBootTest
@AutoConfigureMockMvc
public class MatchHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
//        userRepository.deleteAll();
//        matchRepository.deleteAll();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RiotApiClient riotApiClient() {
            return new FakeRiotApiClient();
        }
    }

    static class FakeRiotApiClient implements RiotApiClient {
        @Override
        public RiotUserInfo getUserInfo(String accessToken) {
            return null; // Not used in this test
        }

        @Override
        public AccountDto getPuuidByRiotId(String gameName, String tagLine) {
            if ("hide with speard".equals(gameName) && "6953".equals(tagLine)) {
                return new AccountDto("blY9VVsEOjfcTC7MkMsO2E7oExt-DpiIG_UkZA1KNW5K5GyORtR1EQyJo-19CP-1ALa_jDrYCF0qvw", gameName, tagLine);
            }
            if ("api-error-user".equals(gameName)) {
                throw new RiotApiException("Riot API is down");
            }
            return null;
        }

        @Override
        public List<String> getMatchIdsByPuuid(String puuid) {
            return IntStream.range(1, 21)
                    .mapToObj(i -> "KR_MATCH_" + i)
                    .collect(Collectors.toList());
        }

        @Override
        public MatchDto getMatchDetailByMatchId(String matchId) {
            return new MatchDto();
        }
    }

    @DisplayName("신규 사용자의 전적을 성공적으로 검색하고 저장한다")
    @Test
    void collectMatchHistory_forNewUser_savesMatchesAndReturnsSuccess() throws Exception {
        // given
        MatchHistoryController.CollectMatchHistoryRequest request = new MatchHistoryController.CollectMatchHistoryRequest("hide with speard", "6953");

        // when & then
        mockMvc.perform(post("/api/matches/by-riot-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("새로운 전적 20건을 성공적으로 저장했습니다."))
                .andExpect(jsonPath("$.savedMatchCount").value(20));

        // then: Verify database state
        Optional<User> savedUser = userRepository.findByPuuid("puuid-123");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getGameName()).isEqualTo("hide with speard");

        List<String> allMatchIdsInDb = matchRepository.findExistingMatchIds(
                IntStream.range(1, 21).mapToObj(i -> "KR_MATCH_" + i).collect(Collectors.toList())
        );
        assertThat(allMatchIdsInDb).hasSize(20);
    }

    @DisplayName("gameName 또는 tagLine이 비어있는 요청 시, 400 Bad Request를 반환한다")
    @ParameterizedTest
    @CsvSource({
            "'', 'KR1'",
            "'testUser', ''",
            ", 'KR1'",
            "'testUser', "
    })
    void collectMatchHistory_withInvalidRequest_returnsBadRequest(String gameName, String tagLine) throws Exception {
        // given
        MatchHistoryController.CollectMatchHistoryRequest request = new MatchHistoryController.CollectMatchHistoryRequest(gameName, tagLine);

        // when & then
        mockMvc.perform(post("/api/matches/by-riot-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("존재하지 않는 라이엇 ID로 요청 시, 404 Not Found를 반환한다")
    @Test
    void collectMatchHistory_withNonExistentRiotId_returnsNotFound() throws Exception {
        // given
        MatchHistoryController.CollectMatchHistoryRequest request = new MatchHistoryController.CollectMatchHistoryRequest("없는유저", "KR1");

        // when & then
        mockMvc.perform(post("/api/matches/by-riot-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("라이엇 API 통신 오류 발생 시, 503 Service Unavailable을 반환한다")
    @Test
    void collectMatchHistory_whenRiotApiFails_returnsServiceUnavailable() throws Exception {
        // given
        MatchHistoryController.CollectMatchHistoryRequest request = new MatchHistoryController.CollectMatchHistoryRequest("api-error-user", "KR1");

        // when & then
        mockMvc.perform(post("/api/matches/by-riot-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable());
    }

    @DisplayName("조회된 모든 경기가 이미 DB에 존재할 경우, '새로운 전적이 없습니다' 메시지를 반환한다")
    @Test
    void collectMatchHistory_whenAllMatchesExist_returnsNoNewMatchesMessage() throws Exception {
        // given
        // FakeRiotApiClient가 반환할 모든 매치 ID를 미리 DB에 저장
        IntStream.range(1, 21).forEach(i -> {
            matchRepository.save(Match.builder().matchId("KR_MATCH_" + i).build());
        });

        MatchHistoryController.CollectMatchHistoryRequest request = new MatchHistoryController.CollectMatchHistoryRequest("hide with speard", "6953");

        // when & then
        mockMvc.perform(post("/api/matches/by-riot-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("업데이트할 새로운 전적이 없습니다."))
                .andExpect(jsonPath("$.savedMatchCount").value(0));
    }

    @DisplayName("기존 사용자의 전적을 조회하여 새로운 경기만 저장한다")
    @Test
    void collectMatchHistory_forExistingUser_savesOnlyNewMatches() throws Exception {
        // given
        // 1. Save the user and 10 out of 20 matches in advance
        userRepository.save(User.create("puuid-123", "hide with speard", "6953"));
        IntStream.range(1, 11).forEach(i -> {
            matchRepository.save(Match.builder().matchId("KR_MATCH_" + i).build());
        });

        MatchHistoryController.CollectMatchHistoryRequest request = new MatchHistoryController.CollectMatchHistoryRequest("hide with speard", "6953");

        // when & then
        mockMvc.perform(post("/api/matches/by-riot-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("새로운 전적 10건을 성공적으로 저장했습니다."))
                .andExpect(jsonPath("$.savedMatchCount").value(10));

        // then: Verify database state
        assertThat(userRepository.count()).isEqualTo(1); // User should not be created again
        assertThat(matchRepository.count()).isEqualTo(20); // 10 new matches should be added
    }
}
