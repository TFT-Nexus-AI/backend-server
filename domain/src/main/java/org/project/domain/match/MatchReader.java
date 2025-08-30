package org.project.domain.match;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MatchReader {

	private final MatchRepository matchRepository;

	public List<Match> read(String puuid, int count) {
		return matchRepository.findByUserPuuidOrderByGameDatetimeDesc(puuid, count);
	}

	public List<Match> readRecent(String puuid, int days) {
		LocalDateTime since = LocalDateTime.now().minusDays(days);
		return matchRepository.findRecentMatches(puuid, since);
	}

	public Optional<Match> find(String matchId) {
		return matchRepository.findByMatchId(matchId);
	}

	public boolean exists(String matchId) {
		return matchRepository.existsByMatchId(matchId);
	}

}
