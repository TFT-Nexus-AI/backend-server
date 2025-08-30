package org.project.client.riot.api;

import lombok.RequiredArgsConstructor;
import org.project.domain.exception.MatchNotFoundException;
import org.project.domain.match.Match;
import org.project.domain.match.RiotMatchClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RiotMatchClientImpl implements RiotMatchClient {
    private final RiotWebClient webClient;

    @Override
    public List<String> getMatchIdsByPuuid(String puuid, int count) {
        return webClient.get()
                .uri("/tft/match/v1/matches/by-puuid/{puuid}/ids?count={count}",
                        puuid, count)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return Mono.error(new MatchNotFoundException(
                            puuid));
                })
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .block();
    }

    @Override
    public Match getMatchDetails(String matchId) {
        MatchDto response = webClient.get()
                .uri("/tft/match/v1/matches/{matchId}", matchId)
                .retrieve()
                .bodyToMono(MatchDto.class)
                .block();

        // DTO → Domain 변환
        return Match.create(
                response.matchId(),
                response.gameDatetime(),
                response.gameLength(),
                response.gameVersion(),
                response.tftSet()


        );
    }
}
