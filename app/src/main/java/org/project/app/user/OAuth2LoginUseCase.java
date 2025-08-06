package org.project.app.user;

import lombok.RequiredArgsConstructor;
import org.project.app.exception.RiotApiException;
import org.springframework.stereotype.Service;

import org.project.domain.user.User;
import org.project.domain.user.UserService;

import org.project.app.exception.LoginException;

@Service
@RequiredArgsConstructor
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class OAuth2LoginUseCase {
    private final UserService userService;
    private final RiotApiClient riotApiClient;

    public OAuth2LoginUseCase(UserService userService, RiotApiClient riotApiClient) {
        this.userService = userService;
        this.riotApiClient = riotApiClient;
    }

    @Transactional
    public LoginResult login(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new IllegalArgumentException("액세스 토큰은 필수입니다");
        }

        try {
            RiotUserInfo riotUserInfo = riotApiClient.getUserInfo(accessToken);
            User user = userService.registerUser(riotUserInfo.getPuuid(), riotUserInfo.getGameName(), riotUserInfo.getTagLine());
            return LoginResult.success(user);
        } catch (RiotApiException e) {
            throw new LoginException("로그인에 실패했습니다: " + e.getMessage(), e);
        }
    }
}
