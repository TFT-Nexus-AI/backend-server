package org.project.domain.match;

import java.util.List;

public interface MatchRepository {
    List<String> findExistingMatchIds(List<String> matchIds);
    Match save(Match match);
    long count();
    void deleteAll();
    List<Match> findAll();
}
