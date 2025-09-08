package org.project.domain.user;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Participant {

    private final String puuid;

    private final int placement;

    private final int level;

    private final int playersEliminated;

    private final float timeEliminated;

    private final int goldLeft;

    private final int totalDamage;

    private final List<Trait> traits;

    private final List<Unit> units;

    private final Companion companion;

    private final boolean isWinner;

    @Builder
    private Participant(String puuid, int placement, int level, int playersEliminated, float timeEliminated, int goldLeft, int totalDamage, List<Trait> traits, List<Unit> units, Companion companion, boolean isWinner) {

        this.puuid = puuid;
        this.placement = placement;
        this.level = level;
        this.playersEliminated = playersEliminated;
        this.timeEliminated = timeEliminated;
        this.goldLeft = goldLeft;
        this.totalDamage = totalDamage;
        this.traits = traits;
        this.units = units;
        this.companion = companion;
        this.isWinner = isWinner;
    }
}
