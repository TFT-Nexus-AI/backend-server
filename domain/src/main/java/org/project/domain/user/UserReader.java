package org.project.domain.user;

import lombok.RequiredArgsConstructor;
import org.project.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserReader {

	private final UserRepository userRepository;

	public User read(String gameName, String tagLine) {
		return userRepository.findByGameNameAndTagLine(gameName, tagLine)
			.orElseThrow(() -> new UserNotFoundException(gameName, tagLine));
	}

	public Optional<User> find(String gameName, String tagLine) {

		return userRepository.findByGameNameAndTagLine(gameName, tagLine);

	}

	public boolean exist(String gameName, String tagLine) {
		return userRepository.existByGameNameAndTagLine(gameName, tagLine);
	}

}
