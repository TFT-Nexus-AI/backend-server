package org.project.storage.db.core.user;

import org.project.domain.user.User;
import org.project.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
		this.userJpaRepository = userJpaRepository;
	}

	@Override
	public boolean existByGameNameAndTagLine(String gameName, String tagLine) {
		return userJpaRepository.existsByGameNameAndTagLine(gameName, tagLine);
	}

	@Override
	public Optional<User> findByGameNameAndTagLine(String gameName, String tagLine) {

		return userJpaRepository.findByGameNameAndTagLine(gameName, tagLine).map(UserEntity::toDomain);
	}

	@Override
	public User save(User user) {
		UserEntity entity = UserEntity.fromDomain(user);
		UserEntity savedEntity = userJpaRepository.save(entity);
		return savedEntity.toDomain();
	}

	@Override
	public boolean existByPuuid(String puuid) {
		return false;
	}

}
