package org.project.client.riot.api;

import org.project.domain.match.vo.MatchData;
import org.project.domain.match.vo.ParticipantData;
import org.project.domain.match.vo.TraitData;
import org.project.domain.match.vo.UnitData;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatchApiMapper {

    /**
     * MatchDto 전체를 domain이 요구하는 MatchData로 번역합니다.
     *
     * @param dto Riot API 응답을 담고 있는 원본 DTO
     * @return 내부 도메인 로직에서 사용될 순수한 값 객체(VO)
     */
    public MatchData toMatchData(MatchDto dto) {
        if (dto == null) {
            return null;
        }

        List<ParticipantData> participants = dto.info() != null && dto.info().participants() != null
                ? dto.info().participants().stream()
                .map(this::toParticipantData)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return new MatchData(
                dto.metadata() != null ? dto.metadata().matchId() : null,
                dto.info() != null ? dto.info().gameDatetime() : 0L,
                dto.info() != null ? dto.info().gameLength() : 0.0f,
                dto.info() != null ? dto.info().gameVersion() : null,
                participants
        );
    }

    /**
     * ParticipantDto를 ParticipantData로 번역합니다.
     */
    private ParticipantData toParticipantData(MatchDto.ParticipantDto dto) {
        if (dto == null) {
            return null;
        }

        List<TraitData> traits = dto.traits() != null
                ? dto.traits().stream().map(this::toTraitData).collect(Collectors.toList())
                : Collections.emptyList();

        List<UnitData> units = dto.units() != null
                ? dto.units().stream().map(this::toUnitData).collect(Collectors.toList())
                : Collections.emptyList();

        return new ParticipantData(
                dto.puuid(),
                dto.placement(),
                dto.level(),
                dto.playersEliminated(),
                dto.timeEliminated(),
                dto.goldLeft(),
                dto.totalDamageToPlayers(),
                traits,
                units
        );
    }

    /**
     * TraitDto를 TraitData로 번역합니다.
     */
    private TraitData toTraitData(MatchDto.TraitDto dto) {
        if (dto == null) {
            return null;
        }
        return new TraitData(dto.name(), dto.numUnits(), dto.tierCurrent());
    }

    /**
     * UnitDto를 UnitData로 번역합니다.
     */
    private UnitData toUnitData(MatchDto.UnitDto dto) {
        if (dto == null) {
            return null;
        }

        // Riot API는 아이템 ID(Integer)를 제공하므로, String으로 변환합니다.
        // 만약 실제 아이템 이름이 필요하다면, 여기서 ID-이름 매핑 테이블을 조회하는 로직을 추가할 수 있습니다.
        List<String> itemNames = dto.itemNames() != null
                ? dto.itemNames().stream().map(String::valueOf).collect(Collectors.toList())
                : Collections.emptyList();

        return new UnitData(dto.characterId(), itemNames, dto.tier());
    }
}