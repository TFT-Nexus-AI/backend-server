package org.project.domain.match;

import lombok.RequiredArgsConstructor;

import org.project.domain.user.User;
import org.project.domain.user.UserProcessor;
import org.project.domain.user.UserReader;
import org.springframework.stereotype.Service;


import java.util.List;



@Service
@RequiredArgsConstructor
public class MatchService {
    private final UserReader userReader;
    private final UserProcessor userProcessor;
    private final MatchReader matchReader;
    private final MatchProcessor matchProcessor;


    public List<Match> getMatches(String gameName, String tagLine, int count) {
        // 1. 사용자 조회 또는 생성
        User user = userProcessor.getOrRegister(gameName, tagLine);

        // 2. 매치 조회 및 동기화
        return matchProcessor.getOrSync(user, count);
    }

    public List<Match> getRecentMatches(String gameName, String tagLine) {
        User user = userReader.read(gameName, tagLine);
        return matchReader.readRecent(user.getPuuid(), 10);
    }
}


