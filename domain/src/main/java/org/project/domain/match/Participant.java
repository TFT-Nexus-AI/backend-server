package org.project.domain.match;

import lombok.Builder;
import lombok.Getter;
import org.project.domain.match.vo.ParticipantData;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Participant {

    private final String puuid;

    private final int placement;

    private final int level;

    private final int playersEliminated;

    private final float timeEliminated;

    private final int goldLeft;

    private final int totalDamage;

    private List<Trait> traits;
    private List<Unit> units;

    @Builder
    private Participant(String puuid, int placement, int level, int playersEliminated, float timeEliminated, int goldLeft, int totalDamage, List<Trait> traits, List<Unit> units) {
        this.puuid = puuid;
        this.placement = placement;
        this.level = level;
        this.playersEliminated = playersEliminated;
        this.timeEliminated = timeEliminated;
        this.goldLeft = goldLeft;
        this.totalDamage = totalDamage;
        this.traits = traits;
        this.units = units;
    }


    public static Participant from(ParticipantData participantData) {
        return Participant.builder()
                .puuid(participantData.puuid())
                .placement(participantData.placement())
                .level(participantData.level())
                .playersEliminated(participantData.playersEliminated())
                .timeEliminated(participantData.timeEliminated())
                .goldLeft(participantData.goldLeft())
                .totalDamage(participantData.totalDamage())
                .traits(participantData.traits().stream().map(Trait::from).collect(Collectors.toList()))
                .units(participantData.units().stream().map(Unit::from).collect(Collectors.toList()))
                .build();
    }
}
