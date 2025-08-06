package org.project.storage.db.core.user;

import lombok.RequiredArgsConstructor;
import org.project.domain.user.User;
import org.project.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByPuuid(String puuid) {
        return userJpaRepository.findByPuuid(puuid)
                .map(this::entityToDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = domainToEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return entityToDomain(savedEntity);
    }

    private User entityToDomain(UserEntity entity) {
        return User.builder()
                .puuid(entity.getPuuid())
                .gameName(entity.getGameName())
                .tagLine(entity.getTagLine())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private UserEntity domainToEntity(User user) {
        return UserEntity.builder()
                .puuid(user.getPuuid())
                .gameName(user.getGameName())
                .tagLine(user.getTagLine())
                .createdAt(user.getCreatedAt())
                .build();
    }


}
