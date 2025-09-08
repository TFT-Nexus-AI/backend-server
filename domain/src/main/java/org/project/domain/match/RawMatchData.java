package org.project.domain.match;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RawMatchData {
    private final Long id;
    private final String matchId;
    private final LocalDateTime fetchedAt;
    private final String apiVersion;
//    private final String checkSum;

    @Builder
    public RawMatchData(Long id, String matchId, LocalDateTime fetchedAt, String apiVersion) {
        this.id = id;
        this.matchId = matchId;
        this.fetchedAt = fetchedAt;
        this.apiVersion = apiVersion;
    }


}
