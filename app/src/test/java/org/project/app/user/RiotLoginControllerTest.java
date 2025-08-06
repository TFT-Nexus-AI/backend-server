package org.project.app.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * - [ ] **실패**: 유효하지 않은 인증 코드(Authorization Code)로 요청 시, 로그인 실패 응답을 반환한다.
 * - [ ] **실패**: 라이엇 API에서 사용자 정보를 가져오는 데 실패 시, 로그인 실패 응답을 반환한다.
 * - [ ] **성공 (신규 사용자)**: 유효한 코드로 첫 로그인 시, 새로운 사용자를 생성하고 성공 응답을 반환한다.
 * - [ ] **성공 (기존 사용자)**: 유효한 코드로 재로그인 시, 기존 사용자의 정보를 업데이트하고 성공 응답을 반환한다.
 * - [ ] **실패 (입력값 오류)**: `code` 파라미터 없이 요청 시, 400 Bad Request 오류를 반환한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RiotLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RiotTokenProvider riotTokenProvider() {
            return new FakeRiotTokenProvider();
        }

        @Bean
        public RiotApiClient riotApiClient() {
            return new MockRiotApiClient();
        }
    }


    @Disabled("아직 기능 구현이 완료되지 않았습니다.")
    @DisplayName("신규 사용자가 라이엇 OAuth2.0으로 성공적으로 로그인한다")
    @Test
    void handleRiotCallback_forNewUser_returnsLoginResult() throws Exception {
        // given
        String authorizationCode = "test-authorization-code";

        // when & then
        mockMvc.perform(get("/login/oauth2/code/riot").param("code", authorizationCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.puuid").value("test-puuid-123"))
                .andExpect(jsonPath("$.user.gameName").value("TestPlayer"))
                .andExpect(jsonPath("$.user.tagLine").value("KR1"));
    }
}
