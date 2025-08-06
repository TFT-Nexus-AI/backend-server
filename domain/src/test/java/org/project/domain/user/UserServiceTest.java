package org.project.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;


    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("새로운 사용자를 등록할 수 있다")
    void registerNewUser() {
        // given
        String puuid = "new-user-puuid";
        String gameName = "NewPlayer";
        String tagLine = "KR1";

        when(userRepository.findByPuuid(puuid)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        User registeredUser = userService.registerUser(puuid, gameName, tagLine);

        // then
        assertThat(registeredUser.getPuuid()).isEqualTo(puuid);
        assertThat(registeredUser.getGameName()).isEqualTo(gameName);
        assertThat(registeredUser.getTagLine()).isEqualTo(tagLine);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 사용자를 등록하려고 하면 기존 사용자를 반환한다")
    void registerExistingUser() {
        // given
        String puuid = "existing-user-puuid";
        String gameName = "ExistingPlayer";
        String tagLine = "KR1";

        User existingUser = User.create(puuid, "OldName", "OldTag");
        when(userRepository.findByPuuid(puuid)).thenReturn(Optional.of(existingUser));

        // when
        User result = userService.registerUser(puuid, gameName, tagLine);

        // then
        assertThat(result).isEqualTo(existingUser);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("puuid로 사용자를 조회할 수 있다")
    void findUserByPuuid() {
        // given
        String puuid = "test-puuid-123";
        User expectedUser = User.create(puuid, "TestPlayer", "KR1");
        when(userRepository.findByPuuid(puuid)).thenReturn(Optional.of(expectedUser));

        // when
        Optional<User> foundUser = userService.findByPuuid(puuid);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(expectedUser);
    }
}
