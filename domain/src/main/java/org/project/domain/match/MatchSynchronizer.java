package org.project.domain.match;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MatchSynchronizer {
    private final RiotMatchClient riotMatchClient;
    private final MatchAppender matchAppender;
    private final MatchReader matchReader;


    public List<Match> sync(String puuid, int count) {

        // Riot API에서 매치 ID 목록 조회
        List<String> matchIds = riotMatchClient.getMatchIdsByPuuid(puuid, count);

        // 이미 저장된 매치 필터링
        List<String> newMatchIds = matchIds.stream()
                .filter(id -> !matchReader.exists(id))
                .collect(Collectors.toList());

        // 새로운 매치만 상세 정보 조회 및 저장
        List<Match> newMatches = new ArrayList<>();
        for (String matchId : newMatchIds) {
            Match match = riotMatchClient.getMatchDetails(matchId);
            Match saved = matchAppender.append(match);
            newMatches.add(saved);
        }

        return newMatches;


    }
}
