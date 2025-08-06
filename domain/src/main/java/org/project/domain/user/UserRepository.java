package org.project.domain.user;

import java.util.Optional;


public interface UserRepository {
    Optional<User> findByPuuid(String puuid);

    User save(User user);
}
