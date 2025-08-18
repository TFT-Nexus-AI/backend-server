package org.project.domain.user;


import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {

    boolean existByGameNameAndTagLine(String gameName, String tagLine);

    Optional<User> findByGameNameAndTagLine(String gameName, String tagLine);

    User save(User user);

}
