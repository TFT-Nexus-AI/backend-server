package org.project.domain.match;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository {
    Optional<Match> findByMatchId(String matchId);

    List<Match> findByPuuid(String puuid);

    Match save(Match match);

    boolean existsByMatchId(String matchId);
}
