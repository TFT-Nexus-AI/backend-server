package org.project.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserRepositoryTest {
  @Autowired
  private UserRepository userRepository;


    @Test
    @DisplayName("동일한 gameName,tagLine을 가진 사용자가 이미 존재한다")
    void existByGameNameAndTagLine() {
     //given
        User existUser = User.create("existing-puuid", "hide with speard", "6953");
        userRepository.save(existUser);
      //when
       boolean exist = userRepository.existByGameNameAndTagLine(existUser.getGameName(), existUser.getTagLine());
      //then
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("새로운 gameName과 tagLine 조합은 존재하지 않음을 확인할 수 있다")
    void newGameNameAndTagLine() {
        // When - 등록되지 않은 사용자로 존재 여부 확인
        boolean exists = userRepository.existByGameNameAndTagLine("new user", "KR1");

        // Then - 존재하지 않음을 확인
        assertThat(exists).isFalse();
    }

}
