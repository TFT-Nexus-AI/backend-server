package org.project.app.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class RiotLoginController {

    private final RiotTokenProvider riotTokenProvider;
    private final OAuth2LoginUseCase oAuth2LoginUseCase;


    @GetMapping("/login/oauth2/code/riot")
    public LoginResult handleRiotCallback(@RequestParam("code") String code) {
        String accessToken = riotTokenProvider.getAccessToken(code);
        return oAuth2LoginUseCase.login(accessToken);
    }
}
