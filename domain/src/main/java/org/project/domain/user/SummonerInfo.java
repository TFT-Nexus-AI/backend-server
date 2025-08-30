package org.project.domain.user;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SummonerInfo {
    private final String id;
    private final String accountId;
    private final String profileIconId;
    private final String summonerLevel;
    private final String puuid;

    @Builder
    public SummonerInfo(String id, String accountId, String puuid, String profileIconId, String summonerLevel) {
        this.id = id;
        this.accountId = accountId;
        this.puuid = puuid;
        this.profileIconId = profileIconId;
        this.summonerLevel = summonerLevel;
    }


    public static SummonerInfo create(String id, String accountId, String puuid, String profileIconId, String summonerLevel) {
        return SummonerInfo.builder()
                .id(id)
                .accountId(accountId)
                .puuid(puuid)
                .profileIconId(profileIconId)
                .summonerLevel(summonerLevel)
                .build();

    }
}
