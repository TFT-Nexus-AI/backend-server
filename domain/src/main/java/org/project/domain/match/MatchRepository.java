package org.project.domain.match;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository {
    List<Match> findByUserPuuidOrderByGameDatetimeDesc(String puuid, int limit);

    Optional<Match> findByMatchId(String matchId);

    Match save(Match match);

    boolean existsByMatchId(String matchId);

    List<Match> findRecentMatches(String puuid, LocalDateTime since);

    List<Match> saveAll(List<Match> newMatches);
}
