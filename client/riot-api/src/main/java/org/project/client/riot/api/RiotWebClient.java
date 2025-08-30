package org.project.client.riot.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.domain.exception.RiotApiException;
import org.project.domain.exception.UserNotFoundException;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

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

    private Mono<? extends Throwable> handleError(ClientResponse response) {
        return response.bodyToMono(String.class)
                // body가 없는 경우를 대비해 defaultIfEmpty("")를 추가하면 더 안정적입니다.
                .defaultIfEmpty("Response body is empty")
                .flatMap(body -> {
                    String errorMessage = String.format("API Error: %s, Response: %s",
                            response.statusCode(), body);

                    // 통합된 핸들러에서 HttpStatus를 담아 예외를 생성합니다.
                    return Mono.error(new RiotApiException(errorMessage,
                            (HttpStatus) response.statusCode()));
                });
    }

}
