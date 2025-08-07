package org.project.app.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * - [ ] **실패 (입력값 오류)**: `gameName` 또는 `tagLine`이 비어있는 요청 시, 400 Bad Request 오류를 반환한다.
 * - [ ] **실패 (사용자 없음)**: 존재하지 않는 라이엇 ID로 요청 시, "사용자를 찾을 수 없습니다" 오류를 반환한다.
 * - [ ] **실패 (API 통신 오류)**: 라이엇 API 통신 중 오류 발생 시, "API 통신 오류"를 반환한다.
 * - [ ] **성공 (신규 전적 없음)**: 조회된 모든 경기가 이미 DB에 존재할 경우, "새로운 전적이 없습니다" 메시지를 반환한다.
 * - [ ] **성공 (신규 사용자 & 신규 전적)**: 신규 사용자의 전적을 처음 조회하여 모두 DB에 저장하고, 저장된 경기 수를 반환한다.
 * - [ ] **성공 (기존 사용자 & 일부 신규 전적)**: 기존 사용자의 전적을 조회하여 새로운 경기만 DB에 저장하고, 새로 저장된 경기 수를 반환한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MatchHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @DisplayName("신규 사용자의 전적을 성공적으로 검색하고 저장한다")
    @Test
    void collectMatchHistory_forNewUser_savesMatchesAndReturnsSuccess() throws Exception {
        // given
        MatchHistoryController.CollectMatchHistoryRequest request = new MatchHistoryController.CollectMatchHistoryRequest("hide with speard", "6953");

        // when & then
        mockMvc.perform(post("/api/matches/by-riot-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("새로운 전적 20건을 성공적으로 저장했습니다."))
                .andExpect(jsonPath("$.savedMatchCount").value(20));
    }
}
