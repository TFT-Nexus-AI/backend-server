package org.project.app.user;

import lombok.RequiredArgsConstructor;
import org.project.app.exception.RiotApiException;
import org.springframework.stereotype.Service;

import org.project.domain.user.User;
import org.project.domain.user.UserService;

import org.project.app.exception.LoginException;

@Service
@RequiredArgsConstructor
public class OAuth2LoginUseCase {
    private final UserService userService;
    private final RiotApiClient riotApiClient;

    public LoginResult login(String accessToken) {
        validateAccessToken(accessToken);

        try {
            RiotUserInfo riotUserInfo = riotApiClient.getUserInfo(accessToken);

            User user = userService.registerUser(
                    riotUserInfo.getPuuid(),
                    riotUserInfo.getGameName(),
                    riotUserInfo.getTagLine()
            );
            return LoginResult.success(user);
        } catch (RiotApiException e) {
            throw new LoginException("로그인에 실패했습니다: " + e.getMessage(), e);
        }
    }

    private void validateAccessToken(String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("액세스 토큰은 필수입니다");
        }
    }

}
