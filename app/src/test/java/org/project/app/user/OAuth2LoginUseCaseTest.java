package org.project.app.user;

import org.project.app.client.RiotApiClient;
import org.project.app.exception.LoginException;
import org.project.app.exception.RiotApiException;
import org.project.domain.user.User;
import org.project.domain.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class OAuth2LoginUseCaseTest {
    private UserService userService;
    private RiotApiClient riotApiClient;
    private OAuth2LoginUseCase oauth2LoginUseCase;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        riotApiClient = mock(RiotApiClient.class);
        oauth2LoginUseCase = new OAuth2LoginUseCase(userService, riotApiClient);
    }

    @Test
    @DisplayName("OAuth2 로그인 성공 시 사용자 정보를 반환한다")
    void loginSuccessWithOAuth2() {
        // given
        String accessToken = "riot-access-token-123";
        RiotApiClient.RiotUserInfo riotUserInfo = new RiotApiClient.RiotUserInfo("test-puuid-123", "TestPlayer", "KR1");
        User expectedUser = User.create("test-puuid-123", "TestPlayer", "KR1");

        when(riotApiClient.getUserInfo(accessToken)).thenReturn(riotUserInfo);
        when(userService.registerUser(riotUserInfo.puuid(), riotUserInfo.gameName(), riotUserInfo.tagLine()))
                .thenReturn(expectedUser);

        // when
        LoginResult result = oauth2LoginUseCase.login(accessToken);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUser().getPuuid()).isEqualTo("test-puuid-123");
        assertThat(result.getUser().getGameName()).isEqualTo("TestPlayer");
        assertThat(result.getUser().getTagLine()).isEqualTo("KR1");
        verify(userService).registerUser("test-puuid-123", "TestPlayer", "KR1");
    }

    @Test
    @DisplayName("유효하지 않은 액세스 토큰으로 로그인 시 실패한다")
    void loginFailWithInvalidAccessToken() {
        // given
        String invalidToken = "invalid-token";
        when(riotApiClient.getUserInfo(invalidToken))
                .thenThrow(new RiotApiException("Invalid access token"));

        // when & then
        assertThatThrownBy(() -> oauth2LoginUseCase.login(invalidToken))
                .isInstanceOf(LoginException.class)
                .hasMessage("로그인에 실패했습니다: Invalid access token");
    }

    @Test
    @DisplayName("null 액세스 토큰으로 로그인 시 실패한다")
    void loginFailWithNullAccessToken() {
        // given
        String nullToken = null;

        // when & then
        assertThatThrownBy(() -> oauth2LoginUseCase.login(nullToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("액세스 토큰은 필수입니다");
    }

    @Test
    @DisplayName("빈 액세스 토큰으로 로그인 시 실패한다")
    void loginFailWithEmptyAccessToken() {
        // given
        String emptyToken = "";

        // when & then
        assertThatThrownBy(() -> oauth2LoginUseCase.login(emptyToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("액세스 토큰은 필수입니다");
    }
}
