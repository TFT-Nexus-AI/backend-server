package org.project.storage.db.core.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.domain.user.User;
import org.project.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository{
    private final UserJpaRepository userJpaRepository;

    @Override
    public void deleteAll() {
        userJpaRepository.deleteAll();
    }

    @Override
    public User save(User user) {
        log.info("Saving user: {}", user.getPuuid());
        UserEntity entity = UserEntity.fromDomain(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        log.info("User saved with ID: {}", savedEntity.getId());
        return savedEntity.toDomain();
    }

    @Override
    public Optional<User> findByPuuid(String puuid) {
        log.info("Finding user by puuid: {}", puuid);
        Optional<User> foundUser = userJpaRepository.findByPuuid(puuid).map(UserEntity::toDomain);
        if (foundUser.isPresent()) {
            log.info("User found: {}", foundUser.get().getPuuid());
        } else {
            log.info("User not found for puuid: {}", puuid);
        }
        return foundUser;
    }

    @Override
    public long count() {
        return userJpaRepository.count();
    }
}
