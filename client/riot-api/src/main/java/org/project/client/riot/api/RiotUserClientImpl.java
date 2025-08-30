package org.project.client.riot.api;


import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.project.domain.user.RiotUserClient;

import org.project.domain.user.SummonerInfo;
import org.project.domain.user.User;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class RiotUserClientImpl implements RiotUserClient {

    private final RiotWebClient webClient;


    @Override
    public User fetchUser(String gameName, String tagLine) {

        AccountDto dto = webClient.getAccount(gameName, tagLine, Region.ASIA);
        return User.create(dto.puuid(), dto.gameName(), dto.tagLine());

    }

    @Override
    public SummonerInfo fetchSummoner(String puuid) {
        SummonerInfoDto dto = webClient.getSummoner(puuid, Region.KR);
        return SummonerInfo.create(dto.id(), dto.accountId(), dto.puuid(), dto.profileIconId(), dto.summonerLevel());
    }
}
