package org.project.app.match.dto;

import lombok.Builder;
import org.project.domain.match.Match;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record MatchPageResponseDto(List<MatchResponseDto> matches, int totalCount, int currentPage, int pageSize,
		boolean hasNext, boolean hasPrevious) {
	public static MatchPageResponseDto of(List<Match> allMatches, int page, int size) {
		List<MatchResponseDto> pageMatches = allMatches.stream()
			.skip(page * size)
			.limit(size)
			.map(MatchResponseDto::from) // DTO 변환도 각자 책임
			.collect(Collectors.toList());

		return MatchPageResponseDto.builder()
			.matches(pageMatches)
			.totalCount(allMatches.size())
			.currentPage(page)
			.pageSize(size)
			.hasNext(allMatches.size() > (page + 1) * size)
			.hasPrevious(page > 0)
			.build();
	}
}
