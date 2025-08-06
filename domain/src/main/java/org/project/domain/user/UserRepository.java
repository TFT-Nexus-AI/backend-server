package org.project.domain.user;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findByPuuid(String puuid);

    void deleteAll();
}
