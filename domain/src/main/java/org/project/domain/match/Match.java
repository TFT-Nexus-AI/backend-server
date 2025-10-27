package org.project.domain.match;

import lombok.Builder;

import lombok.Getter;
import org.project.domain.match.vo.MatchData;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Match {

    private final String id;
    private final long gameDatetime;
    private final float gameLength;
    private final String gameVersion;
    private final List<Participant> participants;


    @Builder
    public Match(String id, long gameDatetime, float gameLength, String gameVersion, List<Participant> participants) {
        this.id = id;
        this.gameDatetime = gameDatetime;
        this.gameLength = gameLength;
        this.gameVersion = gameVersion;
        this.participants = participants;
    }

    public static Match from(MatchData matchData) {
        List<Participant> participants = matchData.participants().stream()
                .map(Participant::from)
                .collect(Collectors.toList());

        return Match.builder()
                .id(matchData.matchId())
                .gameDatetime(matchData.gameDatetime())
                .gameLength(matchData.gameLength())
                .gameVersion(matchData.gameVersion())
                .participants(participants)
                .build();
    }


}
