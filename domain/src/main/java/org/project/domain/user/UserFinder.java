package org.project.domain.user;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFinder {
    private final UserRepository userRepository;
    private final RiotUserClient riotUserClient;


    public User findOrCreateUser(String gameName, String tagLine) {
        Optional<User> user = userRepository.findByGameNameAndTagLine(gameName, tagLine);
        if (user.isPresent()) {
            return user.get();
        }
        User newUser = riotUserClient.getUserFromRiotApi(gameName, tagLine);
        return userRepository.save(newUser);
    }
}
