package org.project.client.riot.api;

import org.project.domain.user.User;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RiotUserClient {
    private final WebClient webClient;


    public RiotUserClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://kr.api.riotgames.com")
                .build();
    }

    public User getUserFromRiotApi(String gameName, String tagLine) {
        // Riot API 호출
        RiotAccountDto response = webClient.get()
                .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}",
                        gameName, tagLine)
                .retrieve()
                .bodyToMono(RiotAccountDto.class)
                .block();

        // DTO → Domain 변환 (client 모듈 책임)
        return User.create(response.puuid(), response.gameName(), response.tagLine());
    }

    record RiotAccountDto(String puuid, String gameName, String tagLine) {
    }
}
