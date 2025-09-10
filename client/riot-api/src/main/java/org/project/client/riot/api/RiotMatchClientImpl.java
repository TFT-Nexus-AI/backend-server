package org.project.client.riot.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.client.riot.api.config.RiotApiProperties;
import org.project.domain.match.Match;
import org.project.domain.match.RiotMatchClient;

import org.project.domain.match.vo.MatchData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiotMatchClientImpl implements RiotMatchClient {

    private final RiotWebClient webClient;

    private final RiotApiProperties properties;

    private final MatchApiMapper mapper;


    /**
     * Match API용 Region 결정 Match API는 지역별 구체적인 엔드포인트 사용
     */
    private Region getMatchRegion() {
        String defaultRegion = properties.defaultRegion();

        try {
            return Region.valueOf(defaultRegion.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid region for Match API: {}, using default ASIA", defaultRegion);
            return Region.ASIA;
        }
    }

    @Override
    public Optional<MatchData> findMatchById(String matchId) {
        Region region = getMatchRegion();
        Optional<MatchDto> matchDtoOptional = Optional.ofNullable(webClient.getMatch(matchId, region));

        return matchDtoOptional.map(mapper::toMatchData);
    }
}
