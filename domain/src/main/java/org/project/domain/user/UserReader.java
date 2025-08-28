package org.project.domain.user;

import lombok.RequiredArgsConstructor;
import org.project.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserReader {
    private final UserRepository userRepository;


    public Optional<User> read(String gameName, String tagLine) {
        try {
            return userRepository.findByGameNameAndTagLine(gameName, tagLine);

        } catch (Exception e) {
            throw new UserNotFoundException(gameName, tagLine);
        }

    }
}
