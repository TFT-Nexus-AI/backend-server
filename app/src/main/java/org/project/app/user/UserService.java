package org.project.app.user;

import lombok.RequiredArgsConstructor;
import org.project.app.client.RiotApiClient;
import org.project.app.exception.LoginException;
import org.project.app.exception.RiotApiException;
import org.project.domain.user.User;
import org.project.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RiotApiClient riotApiClient;


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

    public LoginResult login(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new IllegalArgumentException("액세스 토큰은 필수입니다");
        }

        try {
            RiotApiClient.RiotUserInfo riotUserInfo = riotApiClient.getUserInfo(accessToken);
            User user = registerUser(riotUserInfo.puuid(), riotUserInfo.gameName(), riotUserInfo.tagLine());
            return LoginResult.success(user);
        } catch (RiotApiException e) {
            throw new LoginException("로그인에 실패했습니다: " + e.getMessage(), e);
        }
    }
}
