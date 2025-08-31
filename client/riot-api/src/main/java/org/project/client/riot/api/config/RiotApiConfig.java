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
        // 기본 지역의 URL을 사용
        String baseUrl = properties.baseUrl().account().get(properties.defaultRegion());
        if (baseUrl == null) {
            throw new IllegalArgumentException("No account URL found for default region: " + properties.defaultRegion());
        }
        return webClientBuilder.baseUrl(baseUrl)
                .defaultHeader("X-Riot-Token", properties.key())
                .build();

    }

    @Bean
    @Qualifier("summonerApiWebClient")
    public WebClient summonerApiWebClient(WebClient.Builder webClientBuilder, RiotApiProperties properties) {
        String baseUrl = properties.baseUrl().summoner().get("kr"); // 기본으로 한국 사용
        if (baseUrl == null) {
            throw new IllegalArgumentException("No summoner URL found for region: kr");
        }
        return webClientBuilder.baseUrl(baseUrl)
                .defaultHeader("X-Riot-Token", properties.key())
                .build();
    }

}
