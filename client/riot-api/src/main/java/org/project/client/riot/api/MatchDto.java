package org.project.client.riot.api;


import com.fasterxml.jackson.annotation.JsonProperty; // snake_case 처리를 위함

import java.util.List;

/**
 * Riot API의 Match-v5 응답을 담는 불변 데이터 객체(DTO).
 * 'record'를 사용하여 객체 생성 후 데이터 변경을 원천적으로 차단하고,
 * 외부 데이터가 내부 도메인을 오염시키지 않도록 제어한다.
 */

//public record MatchDto(String matchId, Long gameDatetime, Float gameLength, String gameVersion, int tftSet) {
//}
public record MatchDto(
        MetadataDto metadata,
        InfoDto info
) {
    public record MetadataDto(
            String dataVersion,
            String matchId,
            List<String> participants
    ) {
    }

    public record InfoDto(
            long gameId,
            @JsonProperty("game_datetime")
            long gameDatetime,
            long gameCreation,
            long gameDuration,
            String endOfGameResult,
            @JsonProperty("game_length")
            float gameLength,
            @JsonProperty("game_version")
            String gameVersion,
            @JsonProperty("queue_id")
            int queueId,
            int mapId,
            @JsonProperty("tft_game_type")
            String tftGameType,
            @JsonProperty("tft_set_core_name")
            String tftSetCoreName,
            @JsonProperty("tft_set_number")
            int tftSetNumber,
            List<ParticipantDto> participants

    ) {
    }

    public record ParticipantDto(
            String puuid,
            String riotIdGameName,
            String riotIdTagline,
            int placement,
            @JsonProperty("last_round")
            int lastRound,
            int level,
            @JsonProperty("players_eliminated")
            int playersEliminated,
            @JsonProperty("time_eliminated")
            float timeEliminated,
            @JsonProperty("gold_left")
            int goldLeft,
            @JsonProperty("total_damage_to_players")
            int totalDamageToPlayers,
            boolean win,
            List<TraitDto> traits,
            List<UnitDto> units,
            CompanionDto companion


    ) {
    }

    public record TraitDto(
            String name,
            @JsonProperty("num_units")
            int numUnits,
            int style,
            @JsonProperty("tier_current")
            int tierCurrent,
            @JsonProperty("tier_total")
            int tierTotal
    ) {
    }

    public record UnitDto(
            @JsonProperty("character_id")
            String characterId,
            List<Integer> itemNames,
            String name,
            int rarity,
            int tier
    ) {
    }


    public record CompanionDto(
            @JsonProperty("content_ID")
            String contentId,
            @JsonProperty("item_ID")
            int itemId,
            @JsonProperty("skin_ID")
            int skinId,
            String species

    ){}
}