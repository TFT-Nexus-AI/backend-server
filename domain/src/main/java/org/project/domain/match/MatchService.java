package org.project.domain.match;

import lombok.RequiredArgsConstructor;

import org.project.domain.user.User;
import org.project.domain.user.UserAppender;
import org.project.domain.user.UserReader;
import org.springframework.stereotype.Service;


import java.util.List;



@Service
@RequiredArgsConstructor
public class MatchService {
    private final UserReader userReader;
    private final UserAppender userCreator;
    private final MatchReader matchReader;
    private final MatchProcessor matchProcessor;


    public List<Match> getMatches(String gameName, String tagLine, int count) {
        User user = userReader.read(gameName, tagLine).orElseGet(() -> userCreator.create(gameName, tagLine));

        List<Match> existMatch = matchReader.read(user.getPuuid(), count);


        if (existMatch.size() < count) {
            List<Match> syncedMatches = matchProcessor.syncMatches(user.getPuuid(), count);
            return matchProcessor.mergeAndSort(existMatch, syncedMatches, count);
        }

        return existMatch;
    }
}


