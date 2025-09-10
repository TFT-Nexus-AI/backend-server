package org.project.domain.match;

import org.project.domain.match.vo.MatchData;

import java.util.Optional;

public interface RiotMatchClient {

	Optional<MatchData> findMatchById(String matchId);

}
