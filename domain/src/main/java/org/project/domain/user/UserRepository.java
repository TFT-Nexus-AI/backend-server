package org.project.domain.user;


import java.util.Optional;


public interface UserRepository {

	boolean existByGameNameAndTagLine(String gameName, String tagLine);

	Optional<User> findByGameNameAndTagLine(String gameName, String tagLine);

	User save(User user);

	boolean existByPuuid(String puuid);

}
