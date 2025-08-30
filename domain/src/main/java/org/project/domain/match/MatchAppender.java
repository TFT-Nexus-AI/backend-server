package org.project.domain.match;

import lombok.RequiredArgsConstructor;
import org.project.domain.exception.MatchAlreadyExistsException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MatchAppender {
    private final MatchRepository matchRepository;

    public Match append(Match match) {
        if (matchRepository.existsByMatchId(match.getMatchId())) {
            throw new MatchAlreadyExistsException(match.getMatchId());
        }
        return matchRepository.save(match);
    }

    public List<Match> appendAll(List<Match> matches) {
        // 중복 제거 후 저장
        List<Match> newMatches = matches.stream()
                .filter(m -> !matchRepository.existsByMatchId(m.getMatchId()))
                .collect(Collectors.toList());

        return matchRepository.saveAll(newMatches);
    }
}
