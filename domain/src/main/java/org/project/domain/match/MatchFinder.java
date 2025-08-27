package org.project.domain.match;

import lombok.RequiredArgsConstructor;
import org.project.domain.user.User;
import org.project.domain.user.UserFinder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MatchFinder {
    private final MatchRepository matchRepository;
    private final RiotMatchClient riotMatchClient;
    private final UserFinder userFinder;


    public List<Match> findRecentMatches(String puuid, int count) {
        // 1. 기존 DB에서 매치 조회
        List<Match> existingMatches = matchRepository.findByUserPuuidOrderByGameDatetimeDesc(puuid, count);

        // 2. 충분하지 않으면 Riot API에서 추가 조회
        if (existingMatches.size() < count) {
            List<String> matchIds = riotMatchClient.getMatchIdsByPuuid(puuid, count);
            List<Match> newMatches = syncNewMatches(matchIds);

            // 3. 기존 매치와 새 매치 합쳐서 최신순 정렬
            List<Match> allMatches = new ArrayList<>(existingMatches);
            allMatches.addAll(newMatches);
            allMatches.sort((a, b) -> b.getGameDateTime().compareTo(a.getGameDateTime()));

            return allMatches.stream().limit(count).collect(Collectors.toList());
        }

        return existingMatches;
    }

    public List<Match> findMatchesWithUserValidation(String gameName, String tagLine, int count) {
        User user = userFinder.findOrCreateUser(gameName, tagLine);  // 순환 참조 사용
        return findRecentMatches(user.getPuuid(), count);
    }

    private List<Match> syncNewMatches(List<String> matchIds) {
        return matchIds.stream()
                .filter(matchId -> !matchRepository.existsByMatchId(matchId))
                .map(riotMatchClient::getMatchDetails)
                .map(matchRepository::save)
                .collect(Collectors.toList());
    }

}
