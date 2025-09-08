package org.project.domain.user;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private final Long Id;

    private final String puuid;

    private final String gameName;

    private final String tagLine;

    private final Long summonerLevel;

    private final Integer profileIconId;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;


    @Builder
    public User(Long Id, String puuid, String gameName, String tagLine, Long summonerLevel, Integer profileIconId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.Id = Id;
        this.puuid = puuid;
        this.gameName = gameName;
        this.tagLine = tagLine;
        this.summonerLevel = summonerLevel;
        this.profileIconId = profileIconId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt;
    }

    public static User create(String puuid, String gameName, String tagLine) {
        validatePuuid(puuid);
        validateGameName(gameName);
        validateTagLine(tagLine);

        return User.builder().puuid(puuid).gameName(gameName).tagLine(tagLine).createdAt(LocalDateTime.now()).build();
    }

    public static User update(Long level,Integer iconId){
        return User.builder().summonerLevel(level).profileIconId(iconId).updatedAt(LocalDateTime.now()).build();
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
