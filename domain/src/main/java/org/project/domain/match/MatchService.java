package org.project.domain.match;

import lombok.RequiredArgsConstructor;

import org.project.domain.user.User;
import org.project.domain.user.UserFinder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MatchService {
    private final UserFinder userFinder;
    private final MatchFinder matchFinder;


    public List<Match> getMatches(String gameName, String tagLine) {
        return getMatches(gameName, tagLine, 20);  // 기본 20경기
    }

    public List<Match> getMatches(String gameName, String tagLine, int count) {
        // 비즈니스 흐름: 사용자 검증 후 매치 조회
        return matchFinder.findMatchesWithUserValidation(gameName, tagLine, count);
    }

    public List<Match> getRecentMatches(String gameName, String tagLine) {
        User user = userFinder.findOrCreateUser(gameName, tagLine);
        return matchFinder.findRecentMatches(user.getPuuid(), 10);  // 최근 10경기
    }

    public List<Match> getLongMatches(String gameName, String tagLine) {
        List<Match> allMatches = getMatches(gameName, tagLine);
        return allMatches.stream()
                .filter(Match::isLongMatch) // 40분 이상 경기
                .collect(Collectors.toList());


    }
}