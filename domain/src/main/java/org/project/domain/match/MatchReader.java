package org.project.domain.match;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchReader {
    private final MatchRepository matchRepository;

    public List<Match> read(String puuid, int count) {
        return matchRepository.findByUserPuuidOrderByGameDatetimeDesc(puuid, count);
    }
}
