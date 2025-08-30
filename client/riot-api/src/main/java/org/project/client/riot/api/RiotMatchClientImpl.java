package org.project.client.riot.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.client.riot.api.config.RiotApiProperties;
import org.project.domain.match.Match;
import org.project.domain.match.RiotMatchClient;

import org.springframework.stereotype.Component;


import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiotMatchClientImpl implements RiotMatchClient {

    private final RiotWebClient webClient;
    private final RiotApiProperties properties;

    @Override
    public List<String> getMatchIdsByPuuid(String puuid, int count) {
        Region region = getMatchRegion();

        List<String> matchIds = webClient.getMatchIds(puuid, count, region);
        log.info("Fetched {} match IDs for puuid: {} from region: {}",
                matchIds.size(), puuid, region);

        return matchIds;
    }

    @Override
    public Match getMatchDetails(String matchId) {
        Region region = getMatchRegion();

        MatchDto response = webClient.getMatch(matchId, region);
        log.info("Fetched match details for matchId: {} from region: {}",
                matchId, region);

        // DTO → Domain 변환
        return Match.create(
                response.matchId(),
                response.gameDatetime(),
                response.gameLength(),
                response.gameVersion(),
                response.tftSet()
        );
    }

    /**
     * Match API용 Region 결정
     * Match API는 지역별 구체적인 엔드포인트 사용
     */
    private Region getMatchRegion() {
        String defaultRegion = properties.defaultRegion();

        try {
            return Region.valueOf(defaultRegion.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid region for Match API: {}, using default KR", defaultRegion);
            return Region.KR;
        }
    }
}
