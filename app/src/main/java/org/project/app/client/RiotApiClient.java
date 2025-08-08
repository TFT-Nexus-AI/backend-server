package org.project.app.client;

import java.util.List;

public interface RiotApiClient {

    // For Login (OAuth)
    RiotUserInfo getUserInfo(String accessToken);

    // For Match History
    AccountDto getPuuidByRiotId(String gameName, String tagLine);
    List<String> getMatchIdsByPuuid(String puuid);
    MatchDto getMatchDetailByMatchId(String matchId);

    // DTOs for Riot API responses
    record RiotUserInfo(String puuid, String gameName, String tagLine) {}
    record AccountDto(String puuid, String gameName, String tagLine) {}
    record MatchDto(MetadataDto metadata, InfoDto info) {}
    record MetadataDto(String data_version, String match_id, List<String> participants) {}
    record InfoDto(
            String endOfGameResult,
            long gameCreation,
            long gameId,
            long game_datetime,
            float game_length,
            String game_version,
            int mapId,
            List<ParticipantDto> participants,
            int queue_id,
            String tft_game_type,
            String tft_set_core_name,
            int tft_set_number,
            String game_variation,
            int queueId
    ) {}
    record ParticipantDto(
            String puuid,
            String riotIdGameName,
            String riotIdTagline,
            int placement,
            int last_round,
            int level,
            int players_eliminated,
            float time_eliminated,
            int gold_left,
            int total_damage_to_players,
            List<TraitDto> traits,
            List<UnitDto> units,
            CompanionDto companion,
            Object missions, // MissionDto는 RIOT_API.md에 상세 정의가 없으므로 Object로 정의
            boolean win
    ) {}
    record TraitDto(String name, int num_units, int style, int tier_current, int tier_total) {}
    record UnitDto(String character_id, String name, int rarity, int tier, List<Integer> items, List<String> itemNames, String chosen) {}
    record CompanionDto(String content_ID, int item_ID, int skin_ID, String species) {}
}
