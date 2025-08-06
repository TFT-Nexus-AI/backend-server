package org.project.app.user;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RiotUserInfo {
    private final String puuid;
    private final String gameName;
    private final String tagLine;
}
