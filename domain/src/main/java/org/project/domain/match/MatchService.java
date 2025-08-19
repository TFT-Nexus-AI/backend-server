package org.project.domain.match;

import lombok.RequiredArgsConstructor;

import org.project.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MatchService {
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    public List<Match> getMatch(String gameName, String tagLine) {

    }



}
