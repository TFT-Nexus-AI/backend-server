package org.project.domain.user;

import lombok.RequiredArgsConstructor;
import org.project.domain.exception.UserAlreadyExistException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAppender {

	private final UserRepository userRepository;

	public User append(User user) {
		validateUser(user);
		return userRepository.save(user);
	}

	public User append(String puuid, String gameName, String tagLine) {
		User user = User.create(puuid, gameName, tagLine);
		return userRepository.save(user);
	}

	private void validateUser(User user) {
		if (userRepository.existByPuuid(user.getPuuid())) {
			throw new UserAlreadyExistException(user.getGameName(), user.getTagLine());
		}
	}

}
