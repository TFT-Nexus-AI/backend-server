package org.project.domain.match.vo;


import java.util.List;


public record MatchData
        (String matchId,
         long gameDatetime,
         float gameLength,
         String gameVersion,
         List<ParticipantData> participants) {
}