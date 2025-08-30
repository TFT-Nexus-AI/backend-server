package org.project.client.riot.api;

import org.project.client.riot.api.config.RiotApiProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RiotWebClientFactory {
    private final Map<String, WebClient> accountClients;
    private final Map<String, WebClient> matchClients;
    private final Map<String, WebClient> summonerClients;


    public RiotWebClientFactory(WebClient.Builder webClientBuilder, RiotApiProperties properties) {
        Function<String, WebClient> createClient = (baseUrl) ->
                webClientBuilder.clone()
                        .baseUrl(baseUrl)
                        .defaultHeader("X-Riot-Token", properties.key())
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build();

        this.accountClients = properties.baseUrl().account().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> createClient.apply(e.getValue())));

        this.summonerClients = properties.baseUrl().summoner().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> createClient.apply(e.getValue())));

        this.matchClients = properties.baseUrl().tftMatch().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> createClient.apply(e.getValue())));

    }

    public WebClient getAccountClient(Region region) {
        return getClient(accountClients, region);
    }

    public WebClient getSummonerClient(Region region) {
        return getClient(summonerClients, region);
    }

    public WebClient getMatchClient(Region region) {
        return getClient(matchClients, region);
    }

    private WebClient getClient(Map<String, WebClient> clientMap, Region region) {
        WebClient client = clientMap.get(region.getValue());
        if (client == null) {
            throw new IllegalArgumentException("Unsupported region for this API: " + region);
        }
        return client;
    }
}
