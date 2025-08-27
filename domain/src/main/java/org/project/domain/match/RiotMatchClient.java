package org.project.domain.match;

import java.util.List;

public interface RiotMatchClient {
    List<String> getMatchIdsByPuuid(String puuid, int count);
    Match getMatchDetails(String matchId);
}
