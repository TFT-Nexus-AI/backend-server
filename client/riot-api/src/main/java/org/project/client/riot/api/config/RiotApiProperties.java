package org.project.client.riot.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "riot.api")
public record RiotApiProperties(
        String key,
        BaseUrl baseUrl
) {
    public record BaseUrl(
            Map<String, String> account,
            Map<String, String> summoner,
            Map<String, String> tftMatch
    ) {
    }
}
