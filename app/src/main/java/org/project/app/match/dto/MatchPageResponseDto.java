package org.project.app.match.dto;

import lombok.Builder;
import lombok.Getter;
import org.project.domain.match.Match;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MatchPageResponseDto {
    private final List<MatchResponseDto> matches;
    private final int totalCount;
    private final int currentPage;
    private final int pageSize;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public static MatchPageResponseDto of(List<Match> allMatches, int page, int size) {
        List<MatchResponseDto> pageMatches = allMatches.stream()
                .skip(page * size)
                .limit(size)
                .map(MatchResponseDto::from)  // DTO 변환도 각자 책임
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
