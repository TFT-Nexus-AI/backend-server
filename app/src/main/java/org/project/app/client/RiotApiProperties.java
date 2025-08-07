package org.project.app.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "riot.api")
public record RiotApiProperties(String key, Map<String, String> url) {
}
