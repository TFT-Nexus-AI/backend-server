package org.project.app;

import org.junit.jupiter.api.*;
import org.project.app.exception.MatchNotFoundException;
import org.project.app.exception.UserNotFoundException;
import org.project.domain.match.Match;
import org.project.domain.match.MatchService;
import org.project.domain.match.RiotMatchClient;
import org.project.domain.user.RiotUserClient;
import org.project.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assumptions.assumeThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RiotApiIntegrationTest {
    @Autowired
    private MatchService matchService;

    @Autowired
    private RiotUserClient riotUserClient;

    @Autowired
    private RiotMatchClient riotMatchClient;

    private static String testPuuid;
    private static List<String> testMatchIds;

    @Test
    @Order(1)
    @DisplayName("실제 사용자 조회 - 성공 케이스")
    void should_find_real_user_successfully() {
        // given - 실제 존재하는 사용자
        String gameName = "hide on bush";
        String tagLine = "KR1";

        // when
        User user = riotUserClient.getUserFromRiotApi(gameName, tagLine);
        testPuuid = user.getPuuid();

        // then
        assertThat(user).isNotNull();
        assertThat(user.getPuuid()).isNotEmpty();
        assertThat(user.getGameName()).isEqualTo(gameName);
        assertThat(user.getTagLine()).isEqualTo(tagLine);

        System.out.println("Found user: " + user.getGameName() + "#" + user.getTagLine());
        System.out.println("PUUID: " + user.getPuuid());
    }

    @Test
    @Order(2)
    @DisplayName("존재하지 않는 사용자 조회 - 실패 케이스")
    void should_throw_exception_when_user_not_found() {
        // given - 존재하지 않는 사용자
        String gameName = "ThisUserShouldNotExist";
        String tagLine = "9999";

        // when & then
        assertThatThrownBy(() ->
                riotUserClient.getUserFromRiotApi(gameName, tagLine)
        ).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @Order(3)
    @DisplayName("매치 ID 목록 조회 - 성공 케이스")
    void should_get_match_ids_successfully() {
        // given
        assumeThat(testPuuid).isNotNull();
        int count = 5;

        // when
        List<String> matchIds = riotMatchClient.getMatchIdsByPuuid(testPuuid, count);
        testMatchIds = matchIds;

        // then
        assertThat(matchIds).isNotNull();
        assertThat(matchIds).isNotEmpty();
        assertThat(matchIds).hasSizeLessThanOrEqualTo(count);

        // 매치 ID 형식 검증
        for (String matchId : matchIds) {
            assertThat(matchId).matches("^[A-Z]{2,}_\\d+$");
        }

        System.out.println("Found match IDs: " + matchIds);
    }

    @Test
    @Order(4)
    @DisplayName("매치 상세 정보 조회 - 성공 케이스")
    void should_get_match_details_successfully() {
        // given
        assumeThat(testMatchIds).isNotEmpty();
        String matchId = testMatchIds.get(0);

        // when
        Match match = riotMatchClient.getMatchDetails(matchId);

        // then
        assertThat(match).isNotNull();
        assertThat(match.getMatchId()).isEqualTo(matchId);
        assertThat(match.getGameDateTime()).isNotNull();
        assertThat(match.getGameLength()).isPositive();
        assertThat(match.getGameVersion()).isNotEmpty();
        assertThat(match.getTftSet()).isNotEmpty();

        System.out.println("Match details: " + match.getMatchId());
        System.out.println("Game time: " + match.getGameDateTime());
        System.out.println("Duration: " + match.getGameLength() + "s");
    }

    @Test
    @Order(5)
    @DisplayName("존재하지 않는 매치 조회 - 실패 케이스")
    void should_throw_exception_when_match_not_found() {
        // given - 존재하지 않는 매치 ID
        String invalidMatchId = "KR_999999999999";

        // when & then
        assertThatThrownBy(() ->
                riotMatchClient.getMatchDetails(invalidMatchId)
        ).isInstanceOf(MatchNotFoundException.class)
                .hasMessageContaining("매치 상세정보를 찾을 수 없습니다");
    }

    @Test
    @Order(6)
    @DisplayName("MatchService 전체 플로우 테스트 - 성공 케이스")
    void should_complete_match_service_flow_successfully() {
        // given
        String gameName = "hide on bush";
        String tagLine = "KR1";
        int matchCount = 3;

        // when
        List<Match> matches = matchService.getMatches(gameName, tagLine, matchCount);

        // then
        assertThat(matches).isNotNull();
        assertThat(matches).isNotEmpty();
        assertThat(matches).hasSizeLessThanOrEqualTo(matchCount);

        // 각 매치 검증
        for (Match match : matches) {
            assertThat(match.getMatchId()).matches("^[A-Z]{2,}_\\d+$");
            assertThat(match.getGameDateTime()).isNotNull();
            assertThat(match.getGameLength()).isPositive().isLessThan(7200f);
            assertThat(match.getGameVersion()).isNotEmpty();
            assertThat(match.getTftSet()).isNotEmpty();
        }

        System.out.println("Retrieved " + matches.size() + " matches successfully");
    }

    @Test
    @Order(7)
    @DisplayName("Rate Limit 테스트 - 연속 요청")
    void should_handle_rate_limit_appropriately() {
        // given
        String gameName = "hide on bush";
        String tagLine = "KR1";

        // when - 연속으로 여러 요청 (Rate Limit 테스트)
        List<Long> responseTimes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            long startTime = System.currentTimeMillis();

            assertThatCode(() -> {
                matchService.getMatches(gameName, tagLine, 1);
            }).doesNotThrowAnyException();

            long responseTime = System.currentTimeMillis() - startTime;
            responseTimes.add(responseTime);

            // 요청 간 최소 간격 (Rate Limit 준수)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // then
        System.out.println("Response times: " + responseTimes + "ms");
        assertThat(responseTimes).allSatisfy(time ->
                assertThat(time).isLessThan(10000L) // 10초 이내
        );
    }
}