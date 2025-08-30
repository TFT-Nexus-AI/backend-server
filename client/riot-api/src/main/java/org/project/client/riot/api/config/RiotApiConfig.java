package org.project.client.riot.api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@PropertySource("classpath:client-riot-api.yml")
@EnableConfigurationProperties(RiotApiProperties.class)
public class RiotApiConfig {

    @Bean
    @Qualifier("accountApiWebClient")
    public WebClient accountApiWebClient(WebClient.Builder webClientBuilder, RiotApiProperties properties) {
        return webClientBuilder
                .baseUrl(properties.baseUrl().account().toString()) // account base url 사용
                .defaultHeader("X-Riot-Token", properties.key())
                .build();
    }

    @Bean
    @Qualifier("summonerApiWebClient")
    public WebClient summonerApiWebClient(WebClient.Builder webClientBuilder, RiotApiProperties properties) {
        return webClientBuilder
                .baseUrl(properties.baseUrl().summoner().toString()) // summoner base url 사용
                .defaultHeader("X-Riot-Token", properties.key())
                .build();
    }
}
