package org.project.domain.match;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.domain.match.vo.TraitData;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trait {

    private String name;
    private int numUnits;
    private int tierCurrent;

    @Builder
    private Trait(String name, int numUnits, int tierCurrent) {
        this.name = name;
        this.numUnits = numUnits;
        this.tierCurrent = tierCurrent;
    }

    public static Trait from(TraitData traitData) {
        return Trait.builder()
                .name(traitData.name())
                .numUnits(traitData.numUnits())
                .tierCurrent(traitData.tierCurrent())
                .build();
    }
}
