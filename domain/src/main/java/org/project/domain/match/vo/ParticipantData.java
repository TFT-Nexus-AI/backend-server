package org.project.domain.match.vo;

import java.util.List;

public record ParticipantData(String puuid,
                              int placement,
                              int level,
                              int playersEliminated,
                              float timeEliminated,
                              int goldLeft,
                              int totalDamage,
                              List<TraitData> traits,
                              List<UnitData> units) {
}
