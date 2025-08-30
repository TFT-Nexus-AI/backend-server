package org.project.domain.match;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Builder
@EqualsAndHashCode(of = "matchId")
public class Match {

	private final Long id;

	private final String matchId;

	private final Long gameDatetime;

	private final Float gameLength;

	private final String gameVersion;

	private final String tftSet;

	private Match(Long id, String matchId, Long gameDatetime, Float gameLength, String gameVersion, String tftSet) {
		validateMatchId(matchId);
		validateGameDatetime(gameDatetime);
		validateGameLength(gameLength);
		validateGameVersion(gameVersion);
		validateTftSet(tftSet);

		this.id = id;
		this.matchId = matchId;
		this.gameDatetime = gameDatetime;
		this.gameLength = gameLength;
		this.gameVersion = gameVersion;
		this.tftSet = tftSet;
	}

	public static Match create(String matchId, Long gameDatetime, Float gameLength, String gameVersion, String tftSet) {
		return new Match(null, matchId, gameDatetime, gameLength, gameVersion, tftSet);
	}

	public static Match of(Long id, String matchId, Long gameDatetime, Float gameLength, String gameVersion,
			String tftSet) {
		return new Match(id, matchId, gameDatetime, gameLength, gameVersion, tftSet);
	}

	public LocalDateTime getGameDateTime() {
		return gameDatetime != null ? LocalDateTime.ofInstant(Instant.ofEpochMilli(gameDatetime), ZoneOffset.UTC)
				: null;
	}

	public boolean isRecentMatch() {
		if (gameDatetime == null)
			return false;
		LocalDateTime gameTime = getGameDateTime();
		LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
		return gameTime.isAfter(thirtyDaysAgo);
	}

	public boolean isLongMatch() {
		return gameLength != null && gameLength > 2400; // 40분 이상
	}

	private void validateMatchId(String matchId) {
		if (matchId == null || matchId.trim().isEmpty()) {
			throw new IllegalArgumentException("매치 ID는 필수입니다");
		}
		if (!matchId.matches("^[A-Z]{2}_\\d+$")) {
			throw new IllegalArgumentException("매치 ID 형식이 올바르지 않습니다. 예: KR_1234567890");
		}
	}

	private void validateGameDatetime(Long gameDatetime) {
		if (gameDatetime == null) {
			throw new IllegalArgumentException("게임 시간은 필수입니다");
		}
		if (gameDatetime <= 0) {
			throw new IllegalArgumentException("게임 시간은 양수여야 합니다");
		}
	}

	private void validateGameLength(Float gameLength) {
		if (gameLength == null) {
			throw new IllegalArgumentException("게임 길이는 필수입니다");
		}
		if (gameLength <= 0 || gameLength > 7200) { // 최대 2시간
			throw new IllegalArgumentException("게임 길이는 0초 초과 2시간 이하여야 합니다");
		}
	}

	private void validateGameVersion(String gameVersion) {
		if (gameVersion == null || gameVersion.trim().isEmpty()) {
			throw new IllegalArgumentException("게임 버전은 필수입니다");
		}
	}

	private void validateTftSet(String tftSet) {
		if (tftSet == null || tftSet.trim().isEmpty()) {
			throw new IllegalArgumentException("TFT 세트는 필수입니다");
		}
	}

}
