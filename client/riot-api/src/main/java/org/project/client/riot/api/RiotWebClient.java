package org.project.client.riot.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.domain.exception.MatchNotFoundException;
import org.project.domain.exception.RiotApiException;
import org.project.domain.exception.UserNotFoundException;


import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiotWebClient {
    private final RiotWebClientFactory webClientFactory;


    public AccountDto getAccount(String gameName, String tagLine, Region region) {
        WebClient client = webClientFactory.getAccountClient(region);
        return client.get()
                .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}",
                        gameName, tagLine)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return Mono.error(new UserNotFoundException(
                            gameName, tagLine));
                })
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(AccountDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    public SummonerInfoDto getSummoner(String puuid, Region region) {
        WebClient client = webClientFactory.getSummonerClient(region);

        return client.get()
                .uri("/tft/summoner/v1/summoners/by-puuid/{puuid}", puuid)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(SummonerInfoDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    public List<String> getMatchIds(String puuid, int count, Region region) {
        WebClient client = webClientFactory.getMatchClient(region);
        return client.get()
                .uri("/tft/match/v1/matches/by-puuid/{puuid}/ids?count={count}",
                        puuid, count)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return Mono.error(new MatchNotFoundException(puuid));
                })
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    public MatchDto getMatch(String matchId, Region region) {
        WebClient client = webClientFactory.getMatchClient(region);
        return client.get()
                .uri("/tft/match/v1/matches/{matchId}", matchId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(MatchDto.class)
                .timeout(Duration.ofSeconds(10))
                .block();
    }

    private Mono<? extends Throwable> handleError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("Response body is empty")
                .flatMap(body -> {
                    String errorMessage = String.format("API Error: %s, Response: %s",
                            response.statusCode(), body);

                    return Mono.error(new RiotApiException(errorMessage));
                });
    }

}
