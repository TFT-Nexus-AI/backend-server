package org.project.app.match.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.project.domain.match.Match;

import java.time.LocalDateTime;

/**
 * @param gameLengthFormatted 사용자 친화적인 게임 시간 (분:초)
 */
@Builder
public record MatchResponseDto(String matchId, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime gameDateTime,
		Float gameLength, String gameVersion, String tftSet, Boolean isRecentMatch, Boolean isLongMatch,
		String gameLengthFormatted) {
	public static MatchResponseDto from(Match match) {
		return MatchResponseDto.builder()
			.matchId(match.getMatchId())
			.gameDateTime(match.getGameDateTime())
			.gameLength(match.getGameLength())
			.gameVersion(match.getGameVersion())
			.tftSet(match.getTftSet())
			.isRecentMatch(match.isRecentMatch())
			.isLongMatch(match.isLongMatch())
			.gameLengthFormatted(formatGameLength(match.getGameLength()))
			.build();
	}

	public static String formatGameLength(Float gameLength) {
		if (gameLength == null)
			return "Unknown";

		int minutes = (int) (gameLength / 60);
		int seconds = (int) (gameLength % 60);
		return String.format("%d:%02d", minutes, seconds);
	}
}
