package org.project.domain.match;

import lombok.RequiredArgsConstructor;
import org.project.domain.user.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MatchProcessor {

	private final MatchReader matchReader;

	private final MatchAppender matchAppender;

	private final MatchSynchronizer matchSynchronizer;

	@Transactional
	public List<Match> getOrSync(User user, int count) {
		// 기존 매치 조회
		List<Match> existingMatches = matchReader.read(user.getPuuid(), count);

		// 충분한 데이터가 있으면 바로 반환
		if (existingMatches.size() >= count) {
			return existingMatches.stream().limit(count).collect(Collectors.toList());
		}

		// 부족한 만큼 동기화
		int needed = count - existingMatches.size();
		List<Match> syncedMatches = matchSynchronizer.sync(user.getPuuid(), needed);

		// 병합 후 반환
		return mergeAndSort(existingMatches, syncedMatches, count);
	}

	private List<Match> mergeAndSort(List<Match> existing, List<Match> synced, int limit) {
		return Stream.concat(existing.stream(), synced.stream())
			.distinct()
			.sorted((a, b) -> b.getGameDateTime().compareTo(a.getGameDateTime()))
			.limit(limit)
			.collect(Collectors.toList());
	}

}
