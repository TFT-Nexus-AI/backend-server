package org.project.storage.db.core.match;

import org.project.domain.match.Match;
import org.project.domain.match.MatchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MatchRepositoryImpl implements MatchRepository {
    @Override
    public List<Match> findByUserPuuidOrderByGameDatetimeDesc(String puuid, int limit) {
        return List.of();
    }

    @Override
    public Optional<Match> findByMatchId(String matchId) {
        return Optional.empty();
    }

    @Override
    public Match save(Match match) {
        return null;
    }

    @Override
    public boolean existsByMatchId(String matchId) {
        return false;
    }

    @Override
    public List<Match> findRecentMatches(String puuid, LocalDateTime since) {
        return List.of();
    }

    @Override
    public List<Match> saveAll(List<Match> newMatches) {
        return List.of();
    }
}
