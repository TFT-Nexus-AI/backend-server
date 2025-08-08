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
import org.project.app.match.dto.CollectMatchHistoryRequest;
import org.project.app.match.dto.CollectMatchHistoryResponse;
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
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        userRepository.deleteAll();
        matchRepository.deleteAll();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RiotApiClient riotApiClient() {
            return new FakeRiotApiClient();
        }
    }

    static class FakeRiotApiClient implements RiotApiClient {
        public static final String EXISTING_USER_PUUID = "puuid-234";
        public static final String EXISTING_USER_GAMENAME = "k사원";
        public static final String EXISTING_USER_TAGLINE = "7924";

        @Override
        public RiotUserInfo getUserInfo(String accessToken) {
            return null; // Not used in this test
        }

        @Override
        public AccountDto getPuuidByRiotId(String gameName, String tagLine) {
            if ("k사원".equals(gameName) && "7924".equals(tagLine)) {
                return new AccountDto("puuid-234", gameName, tagLine);
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
            return new MatchDto(
                    new RiotApiClient.MetadataDto("data_version", matchId, List.of("puuid1", "puuid2")),
                    new RiotApiClient.InfoDto(
                            "endOfGameResult",
                            1234567890L,
                            123L, // gameId
                            1234567890L, // game_datetime (example value)
                            123.4f, // game_length
                            "gameVersion",
                            123,
                            new java.util.ArrayList<RiotApiClient.ParticipantDto>(), // participants
                            123,
                            "tft_game_type",
                            "tft_set_core_name",
                            123,
                            "game_variation",
                            123
                    )
            );
        }
    }

    @DisplayName("신규 사용자의 전적을 성공적으로 검색하고 저장한다")
    @Test
    void collectMatchHistory_forNewUser_savesMatchesAndReturnsSuccess() throws Exception {
        // given
        CollectMatchHistoryRequest request = new CollectMatchHistoryRequest(FakeRiotApiClient.EXISTING_USER_GAMENAME,
                FakeRiotApiClient.EXISTING_USER_TAGLINE);

        // when & then
        mockMvc.perform(post("/api/matches/by-riot-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("새로운 전적 20건을 성공적으로 저장했습니다."))
                .andExpect(jsonPath("$.savedMatchCount").value(20));

        // then: Verify database state
        Optional<User> savedUser = userRepository.findByPuuid(FakeRiotApiClient.EXISTING_USER_PUUID);
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getGameName()).isEqualTo(FakeRiotApiClient.EXISTING_USER_GAMENAME);
        assertThat(matchRepository.count()).isEqualTo(20);
        // Verify saved match details
        matchRepository.findAll().forEach(match -> {
            assertThat(match.getGameDatetime()).isNotNull();
            assertThat(match.getGameLength()).isNotNull();
            assertThat(match.getGameVersion()).isNotNull();
            assertThat(match.getTftSet()).isNotNull();
        });

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
        CollectMatchHistoryRequest request = new CollectMatchHistoryRequest(gameName, tagLine);

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
        CollectMatchHistoryRequest request = new CollectMatchHistoryRequest("없는유저", "KR1");

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
        CollectMatchHistoryRequest request = new CollectMatchHistoryRequest("api-error-user", "KR1");

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

        CollectMatchHistoryRequest request = new CollectMatchHistoryRequest("k사원", "7924");

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
        userRepository.save(User.create(FakeRiotApiClient.EXISTING_USER_PUUID,
                FakeRiotApiClient.EXISTING_USER_GAMENAME,
                FakeRiotApiClient.EXISTING_USER_TAGLINE));
        IntStream.range(1, 11).forEach(i -> {
            matchRepository.save(Match.builder()
                    .matchId("KR_MATCH_" + i)
                    .gameDatetime(1234567890L)
                    .gameLength(123.4f)
                    .gameVersion("gameVersion")
                    .tftSet("tft_set_core_name")
                    .build());
        });

        // when: "k사원"으로 컨트롤러 호출
        CollectMatchHistoryRequest request = new CollectMatchHistoryRequest(
                FakeRiotApiClient.EXISTING_USER_GAMENAME,
                FakeRiotApiClient.EXISTING_USER_TAGLINE
        );

        // then
        mockMvc.perform(post("/api/matches/by-riot-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.savedMatchCount").value(10));

        // then: DB 상태 검증
        assertThat(userRepository.count()).isEqualTo(1); // 유저는 새로 생성되지 않아야 함
        assertThat(matchRepository.count()).isEqualTo(20); // 10개의 새로운 경기만 추가되어야 함
        // Verify saved match details
        matchRepository.findAll().forEach(match -> {
            assertThat(match.getGameDatetime()).isNotNull();
            assertThat(match.getGameLength()).isNotNull();
            assertThat(match.getGameVersion()).isNotNull();
            assertThat(match.getTftSet()).isNotNull();
        });
    }
}
