package org.project.domain.match;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Match {
    private Long id;
    private String matchId;
    private Long gameDatetime;
    private Float gameLength;
    private String gameVersion;
    private String tftSet;
}
