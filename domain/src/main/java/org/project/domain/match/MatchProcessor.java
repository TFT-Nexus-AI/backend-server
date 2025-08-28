package org.project.domain.match;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MatchProcessor {
    private final MatchRepository matchRepository;
    private final RiotMatchClient riotMatchClient;

    public List<Match> syncMatches(String puuid, int count) {
        List<String> matchIds = riotMatchClient.getMatchIdsByPuuid(puuid, count);
        return syncNewMatches(matchIds);
    }

    public List<Match> mergeAndSort(List<Match> existing, List<Match> synced, int limit) {
        List<Match> combined = new ArrayList<>(existing);
        combined.addAll(synced);

        return combined.stream()
                .sorted((a, b) -> b.getGameDateTime().compareTo(a.getGameDateTime()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    public List<Match> filterLongMatches(List<Match> matches) {
        return matches.stream()
                .filter(Match::isLongMatch)
                .collect(Collectors.toList());
    }

    private List<Match> syncNewMatches(List<String> matchIds) {
        return matchIds.stream()
                .filter(matchId -> !matchRepository.existsByMatchId(matchId))
                .map(riotMatchClient::getMatchDetails)
                .map(matchRepository::save)
                .collect(Collectors.toList());
    }



}
