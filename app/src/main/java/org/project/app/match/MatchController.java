package org.project.app.match;

import lombok.RequiredArgsConstructor;
import org.project.app.match.dto.MatchPageResponseDto;
import org.project.app.match.dto.MatchResponseDto;
import org.project.domain.match.Match;
import org.project.domain.match.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
public class MatchController {

	private final MatchService matchService;

	@GetMapping("/{gameName}/{tagLine}")
	public ResponseEntity<List<MatchResponseDto>> getMatches(@PathVariable String gameName,
			@PathVariable String tagLine, @RequestParam(defaultValue = "20") int count) {
		List<Match> matches = matchService.getMatches(gameName, tagLine, count);
		List<MatchResponseDto> response = matches.stream().map(MatchResponseDto::from).collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{gameName}/{tagLine}/page")
	public ResponseEntity<MatchPageResponseDto> getMatchesWithPaging(@PathVariable String gameName,
			@PathVariable String tagLine, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {

		List<Match> allMatches = matchService.getMatches(gameName, tagLine, (page + 1) * size + 10);
		MatchPageResponseDto response = MatchPageResponseDto.of(allMatches, page, size);

		return ResponseEntity.ok(response);
	}

}
