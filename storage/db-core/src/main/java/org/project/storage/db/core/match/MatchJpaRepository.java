package org.project.storage.db.core.match;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchJpaRepository extends JpaRepository<MatchEntity, Long> {
    List<MatchEntity> findByUserPuuidOrderByGameDatetimeDesc(String puuid, int limit);

    Optional<MatchEntity> findByMatchId(String matchId);

    boolean existsByMatchId(String matchId);

    List<MatchEntity> findByPuuidAndGameDatetimeGreaterThan(String puuid, Long since);
}
