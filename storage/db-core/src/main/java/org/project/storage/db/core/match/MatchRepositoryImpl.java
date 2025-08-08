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
        MatchEntity entity = MatchEntity.builder()
                .matchId(match.getMatchId())
                .gameDatetime(match.getGameDatetime())
                .gameLength(match.getGameLength())
                .gameVersion(match.getGameVersion())
                .tftSet(match.getTftSet())
                .build();
        matchJpaRepository.save(entity);
        return match;
    }

    @Override
    public List<Match> findAll() {
        return matchJpaRepository.findAll().stream()
                .map(entity -> Match.builder()
                        .id(entity.getId())
                        .matchId(entity.getMatchId())
                        .gameDatetime(entity.getGameDatetime())
                        .gameLength(entity.getGameLength())
                        .gameVersion(entity.getGameVersion())
                        .tftSet(entity.getTftSet())
                        .build())
                .collect(java.util.stream.Collectors.toList());
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
