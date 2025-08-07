package org.project.app.user;

import org.project.app.client.RiotApiClient;
import org.project.app.exception.RiotApiException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"default", "test"}) // 기본 및 테스트 환경에서만 사용
public class MockRiotApiClient implements RiotApiClient {

    @Override
    public RiotUserInfo getUserInfo(String accessToken) {
        // 테스트/개발용 더미 데이터
        if ("valid-token".equals(accessToken)) {
            return new RiotUserInfo("mock-puuid-123", "MockPlayer", "KR1");
        }

        throw new RiotApiException("Invalid access token");
    }

    @Override
    public AccountDto getPuuidByRiotId(String gameName, String tagLine) {
        return null;
    }

    @Override
    public List<String> getMatchIdsByPuuid(String puuid) {
        return List.of();
    }

    @Override
    public MatchDto getMatchDetailByMatchId(String matchId) {
        return null;
    }
}
