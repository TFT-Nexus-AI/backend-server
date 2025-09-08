package org.project.domain.match;

import lombok.Builder;

import lombok.Getter;
import org.project.domain.user.Participant;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Getter
public class Match {

    private final Long id;

    private final String matchId;

    private final int queueId;

    private final Long gameId;

    private final String dataVersion;

    private final Long gameCreation;

    private final Long gameDatetime;

    private final Float gameLength;

    private final String gameVersion;

    private final int tftSetNumber;

    private final List<Participant> participants;


    @Builder
    private Match(Long id, String matchId, int queueId, Long gameId, String dataVersion, Long gameCreation, Long gameDatetime, Float gameLength, String gameVersion, int tftSetNumber, List<Participant> participants) {
        this.queueId = queueId;
        this.gameId = gameId;
        this.dataVersion = dataVersion;
        this.gameCreation = gameCreation;
        this.tftSetNumber = tftSetNumber;
        this.participants = participants;
        validateMatchId(matchId);
        validateGameDatetime(gameDatetime);
        validateGameLength(gameLength);
        validateGameVersion(gameVersion);
        validateTftSet(tftSetNumber);

        this.id = id;
        this.matchId = matchId;
        this.gameDatetime = gameDatetime;
        this.gameLength = gameLength;
        this.gameVersion = gameVersion;

    }

    public static Match create(String matchId, Long gameDatetime, Float gameLength, String gameVersion) {
        return Match.builder().matchId(matchId).gameDatetime(gameDatetime).gameLength(gameLength).gameVersion(gameVersion).build();
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

    private void validateTftSet(int tftSet) {
        if (tftSet == 0) {
            throw new IllegalArgumentException("TFT 세트는 필수입니다");
        }
    }

}
