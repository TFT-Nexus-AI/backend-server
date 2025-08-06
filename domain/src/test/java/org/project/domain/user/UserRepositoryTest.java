package org.project.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    private final UserRepository userRepository = mock(UserRepository.class);

    @Test
    @DisplayName("puuid로 사용자를 조회할 수 있다")
    void findUserByPuuid() {
        //given
        String puuid = "test-puuid-123";
        User expectedUser = User.create(puuid, "TestPlayer", "KR1");
        when(userRepository.findByPuuid(puuid)).thenReturn(Optional.of(expectedUser));

        //when
        Optional<User> foundUser = userRepository.findByPuuid(puuid);

        //then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getPuuid()).isEqualTo(puuid);
        assertThat(foundUser.get().getGameName()).isEqualTo("TestPlayer");
        assertThat(foundUser.get().getTagLine()).isEqualTo("KR1");


    }

    @Test
    @DisplayName("존재하지 않는 puuid로 조회하면 빈 Optional을 반환한다")
    void findUserByNonExistentPuuid() {
        // given
        String nonExistentPuuid = "non-existent-puuid";
        when(userRepository.findByPuuid(nonExistentPuuid)).thenReturn(Optional.empty());

        // when
        Optional<User> foundUser = userRepository.findByPuuid(nonExistentPuuid);

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("사용자를 저장할 수 있다")
    void saveUser() {
        // given
        User userToSave = User.create("test-puuid-123", "TestPlayer", "KR1");
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        // when
        User savedUser = userRepository.save(userToSave);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPuuid()).isEqualTo("test-puuid-123");
        verify(userRepository).save(userToSave);
    }

}
