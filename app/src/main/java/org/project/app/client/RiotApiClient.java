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
    record MatchDto(/* fields... */) {}
}
