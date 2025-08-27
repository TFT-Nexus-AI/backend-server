package org.project.client.riot.api;

import org.project.app.exception.MatchNotFoundException;
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
public class RiotMatchClientImpl implements RiotMatchClient {
    private final WebClient webClient;

    public RiotMatchClientImpl(WebClient.Builder webClientBuilder,  @Value("${riot.api.url.asia}") String riotApiUrl, @Value("${riot.api.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl(riotApiUrl)
                .defaultHeader("X-Riot-Token", apiKey)
                .build();
    }

    @Override
    public List<String> getMatchIdsByPuuid(String puuid, int count) {
        return webClient.get()
                .uri("/tft/match/v1/matches/by-puuid/{puuid}/ids?count={count}",
                        puuid, count)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return Mono.error(new MatchNotFoundException(
                            "매치를 찾을 수 없습니다: " + puuid));
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
