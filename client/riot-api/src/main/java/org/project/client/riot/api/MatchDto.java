package org.project.client.riot.api;

public record MatchDto(String matchId, Long gameDatetime, Float gameLength, String gameVersion, String tftSet) {
}
