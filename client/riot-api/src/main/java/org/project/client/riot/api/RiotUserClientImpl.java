package org.project.client.riot.api;


import org.project.domain.exception.RiotApiException;
import org.project.domain.exception.UserNotFoundException;
import org.project.domain.user.RiotUserClient;
import org.project.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class RiotUserClientImpl implements RiotUserClient {
    private final WebClient webClient;


    public RiotUserClientImpl(WebClient.Builder webClientBuilder,
                              @Value("${riot.api.url.asia}") String riotApiUrl,
                              @Value("${riot.api.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl(riotApiUrl)
                .defaultHeader("X-Riot-Token", apiKey)
                .build();
    }

    @Override
    public User getUserFromRiotApi(String gameName, String tagLine) {
        // Riot API 호출
        RiotAccountDto response = webClient.get()
                .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}",
                        gameName, tagLine)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return Mono.error(new UserNotFoundException(
                            gameName, tagLine));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    return Mono.error(new RiotApiException());
                })
                .bodyToMono(RiotAccountDto.class)
                .block();

        // DTO → Domain 변환 (client 모듈 책임)
        return User.create(response.puuid(), response.gameName(), response.tagLine());
    }


}
