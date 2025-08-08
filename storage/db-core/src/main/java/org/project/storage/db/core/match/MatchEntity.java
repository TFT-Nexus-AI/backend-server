package org.project.storage.db.core.match;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "matches")
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String matchId;
    private Long gameDatetime;
    private Float gameLength;
    private String gameVersion;
    private String tftSet;

    @Builder
    private MatchEntity(String matchId, Long gameDatetime, Float gameLength, String gameVersion, String tftSet) {
        this.matchId = matchId;
        this.gameDatetime = gameDatetime;
        this.gameLength = gameLength;
        this.gameVersion = gameVersion;
        this.tftSet = tftSet;
    }
}
