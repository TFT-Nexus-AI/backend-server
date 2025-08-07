package org.project.storage.db.core.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchJpaRepository extends JpaRepository<MatchEntity, Long> {
    @Query("SELECT m.matchId FROM MatchEntity m WHERE m.matchId IN :matchIds")
    List<String> findExistingMatchIds(@Param("matchIds") List<String> matchIds);
}
