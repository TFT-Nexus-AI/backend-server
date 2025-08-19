package org.project.storage.db.core.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    boolean existsByGameNameAndTagLine(String gameName, String tagLine);

    Optional<UserEntity> findByGameNameAndTagLine(String gameName, String tagLine);



}
