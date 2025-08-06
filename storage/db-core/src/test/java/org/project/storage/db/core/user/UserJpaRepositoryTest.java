package org.project.storage.db.core.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;



import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserJpaRepositoryTest {


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("puuid로 사용자를 조회할 수 있다")
    void findByPuuid() {
        // given
        UserEntity userEntity = UserEntity.builder()
                .puuid("test-puuid-123")
                .gameName("TestPlayer")
                .tagLine("KR1")
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(userEntity);

        // when
        Optional<UserEntity> foundUser = userJpaRepository.findByPuuid("test-puuid-123");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getPuuid()).isEqualTo("test-puuid-123");
        assertThat(foundUser.get().getGameName()).isEqualTo("TestPlayer");
    }

    @Test
    @DisplayName("존재하지 않는 puuid로 조회하면 빈 Optional을 반환한다")
    void findByNonExistentPuuid() {
        // when
        Optional<UserEntity> foundUser = userJpaRepository.findByPuuid("non-existent-puuid");

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("사용자를 저장할 수 있다")
    void saveUser() {
        // given
        UserEntity userEntity = UserEntity.builder()
                .puuid("save-test-puuid")
                .gameName("SaveTestPlayer")
                .tagLine("KR1")
                .createdAt(LocalDateTime.now())
                .build();

        // when
        UserEntity savedUser = userJpaRepository.save(userEntity);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getPuuid()).isEqualTo("save-test-puuid");

        // DB에서 다시 조회해서 확인
        Optional<UserEntity> foundUser = userJpaRepository.findByPuuid("save-test-puuid");
        assertThat(foundUser).isPresent();
    }

}
