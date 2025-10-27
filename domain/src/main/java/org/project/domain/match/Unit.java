package org.project.domain.match;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.domain.match.vo.UnitData;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Unit {
    private String characterId;
    private List<String> itemNames;
    private int tier;

    @Builder
    private Unit(String characterId, List<String> itemNames, int tier) {
        this.characterId = characterId;
        this.itemNames = itemNames;
        this.tier = tier;
    }

    public static Unit from(UnitData unitData) {
        return Unit.builder()
                .characterId(unitData.characterId())
                .itemNames(unitData.itemNames())
                .tier(unitData.tier())
                .build();
    }
}