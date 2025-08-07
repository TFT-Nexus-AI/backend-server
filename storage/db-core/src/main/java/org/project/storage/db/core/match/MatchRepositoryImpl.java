package org.project.storage.db.core.match;

import lombok.RequiredArgsConstructor;
import org.project.domain.match.Match;
import org.project.domain.match.MatchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MatchRepositoryImpl implements MatchRepository {

    private final MatchJpaRepository matchJpaRepository;

    @Override
    public List<String> findExistingMatchIds(List<String> matchIds) {
        return matchJpaRepository.findExistingMatchIds(matchIds);
    }

    @Override
    public Match save(Match match) {
        MatchEntity entity = MatchEntity.builder().matchId(match.getMatchId()).build();
        matchJpaRepository.save(entity);
        // For simplicity, we don't map back to domain object yet
        return match;
    }

    @Override
    public long count() {
        return matchJpaRepository.count();
    }

    @Override
    public void deleteAll() {
        matchJpaRepository.deleteAll();
    }
}
