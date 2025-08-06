package org.project.domain.user;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private final String puuid;
    private final String gameName;
    private final String tagLine;
    private final LocalDateTime createdAt;

    @Builder
    public User(String puuid, String gameName, String tagLine) {
        this.puuid = puuid;
        this.gameName = gameName;
        this.tagLine = tagLine;
        this.createdAt = LocalDateTime.now();
    }

    public static User create(String puuid, String gameName, String tagLine) {
        validatePuuid(puuid);
        validateGameName(gameName);
        validateTagLine(tagLine);

        return new User(puuid, gameName, tagLine);
    }

    private static void validateTagLine(String tagLine) {
        if (tagLine == null || tagLine.trim().isEmpty()) {
            throw new IllegalArgumentException("태그 라인은 필수입니다");

        }
    }

    private static void validateGameName(String gameName) {
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new IllegalArgumentException("게임 이름은 필수입니다");
        }

    }

    private static void validatePuuid(String puuid) {
        if (puuid == null || puuid.trim().isEmpty()) {
            throw new IllegalArgumentException("puuid는 필수입니다");
        }

    }
}
