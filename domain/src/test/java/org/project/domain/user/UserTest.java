package org.project.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest {
    @Test
    @DisplayName("유효한 정보로 사용자를 생성할 수 있다")
    void createUserWithValidInfo() {
        //given
        String puuid = "test-puuid-123";
        String gameName = "TestPlayer";
        String tagLine = "KR1";

        //when
        User user = User.create(puuid, gameName, tagLine);

        //then
        assertThat(user.getPuuid()).isEqualTo(puuid);
        assertThat(user.getGameName()).isEqualTo(gameName);
        assertThat(user.getTagLine()).isEqualTo(tagLine);
        assertThat(user.getCreatedAt()).isNotNull();

    }

    @Test
    @DisplayName("puuid가 null이면 사용자 생성에 실패한다")
    void failToCreateUserWithNullPuuid() {
        //given
        String puuid = null;
        String gameName = "TestPlayer";
        String tagLine = "KR1";

        //when
        assertThatThrownBy(() -> User.create(puuid, gameName, tagLine)).isInstanceOf(IllegalArgumentException.class).hasMessage("puuid는 필수입니다");

    }


    @Test
    @DisplayName("gameName이 null이면 사용자 생성에 실패한다")
    void failToCreateUserWithNullGameName() {
        // given
        String puuid = "test-puuid-123";
        String gameName = null;
        String tagLine = "KR1";

        // when & then
        assertThatThrownBy(() -> User.create(puuid, gameName, tagLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게임 이름은 필수입니다");
    }

    @Test
    @DisplayName("tagLine이 null이면 사용자 생성에 실패한다")
    void failToCreateUserWithNullTagLine() {
        // given
        String puuid = "test-puuid-123";
        String gameName = "TestPlayer";
        String tagLine = null;

        // when & then
        assertThatThrownBy(() -> User.create(puuid, gameName, tagLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("태그 라인은 필수입니다");
    }

}
