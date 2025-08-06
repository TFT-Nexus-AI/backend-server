package org.project.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    @Transactional
    public User registerUser(String puuid, String gameName, String tagLine) {
        Optional<User> existingUser = userRepository.findByPuuid(puuid);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User newUser = User.create(puuid, gameName, tagLine);
        return userRepository.save(newUser);
    }

    public Optional<User> findByPuuid(String puuid) {
        return userRepository.findByPuuid(puuid);
    }


}
