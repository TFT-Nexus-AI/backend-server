package org.project.domain.user;

public interface RiotUserClient {
    User fetchUser(String gameName, String tagLine);
    SummonerInfo fetchSummoner(String puuid);
}
