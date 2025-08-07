package org.project.app.user;

import org.project.app.client.RiotApiClient;
import org.project.app.exception.LoginException;
import org.project.app.exception.RiotApiException;
import org.project.domain.user.User;
import org.project.domain.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OAuth2LoginUseCase {
    private final UserService userService;
    private final RiotApiClient riotApiClient;

    public OAuth2LoginUseCase(UserService userService, RiotApiClient riotApiClient) {
        this.userService = userService;
        this.riotApiClient = riotApiClient;
    }

    public LoginResult login(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new IllegalArgumentException("액세스 토큰은 필수입니다");
        }

        try {
            RiotApiClient.RiotUserInfo riotUserInfo = riotApiClient.getUserInfo(accessToken);
            User user = userService.registerUser(riotUserInfo.puuid(), riotUserInfo.gameName(), riotUserInfo.tagLine());
            return LoginResult.success(user);
        } catch (RiotApiException e) {
            throw new LoginException("로그인에 실패했습니다: " + e.getMessage(), e);
        }
    }
}
