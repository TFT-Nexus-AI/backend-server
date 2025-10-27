package org.project.storage.db.core.match;

import jakarta.persistence.*;
import lombok.*;
import org.project.domain.match.Match;
import org.project.storage.db.core.BaseEntity;
import org.project.storage.db.core.CreatableEntity;

@Getter
@Setter
@Entity
@Table(name = "matches")
public class MatchEntity extends CreatableEntity {


    @Column(unique = true, nullable = false, length = 50)
    private String matchId;

    @Column(nullable = false)
    private Long gameDatetime;

    @Column(nullable = false)
    private Float gameLength;

    @Column(nullable = false, length = 20)
    private String gameVersion;

    @Column(nullable = false, length = 50)
    private int tftSet;

//    @Builder
//    private MatchEntity(String matchId, Long gameDatetime, Float gameLength, String gameVersion, int tftSet) {
//        this.matchId = matchId;
//        this.gameDatetime = gameDatetime;
//        this.gameLength = gameLength;
//        this.gameVersion = gameVersion;
//        this.tftSet = tftSet;
//    }

    public  Match toDomain() {
        return Match.builder()
                .id(matchId)
                .gameDatetime(gameDatetime)
                .gameLength(gameLength)
                .gameVersion(gameVersion)
                .build();
    }


    public static MatchEntity fromDomain(Match match) {
       MatchEntity entity = new MatchEntity();
       entity.matchId = match.getId();
       entity.gameDatetime = match.getGameDatetime();
       entity.gameLength = match.getGameLength();
       entity.gameVersion = match.getGameVersion();
       return entity;
    }

}
