package org.project.client.riot.api;

import org.project.domain.user.SummonerInfo;
import org.project.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class RiotAccountMapper {
    User toDomain(AccountDto dto) {
        return User.create(
                dto.puuid(),
                dto.gameName(),
                dto.tagLine()
        );
    }

    SummonerInfo toDomain(SummonerInfoDto dto) {
        return SummonerInfo.create(
                dto.getId(),
                dto.getAccountId(),
                dto.getPuuid(),
                dto.getProfileIconId(),
                dto.getSummonerLevel()
        );
    }
}
