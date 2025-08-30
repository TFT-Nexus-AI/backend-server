package org.project.domain.user;

import lombok.RequiredArgsConstructor;
import org.project.domain.exception.UserAlreadyExistException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserProcessor {

	private final UserReader userReader;

	private final UserAppender userAppender;

	private final RiotUserClient riotUserClient;

	@Transactional
	public User getOrRegister(String gameName, String tagLine) {
		// 기존 사용자 조회
		Optional<User> existingUser = userReader.find(gameName, tagLine);
		if (existingUser.isPresent()) {
			return existingUser.get();
		}

		// Riot API에서 사용자 정보 조회 후 등록
		User newUser = riotUserClient.fetchUser(gameName, tagLine);
		return userAppender.append(newUser);
	}

	@Transactional
	public User register(String gameName, String tagLine) {
		if (userReader.exist(gameName, tagLine)) {
			throw new UserAlreadyExistException(gameName, tagLine);
		}

		User user = riotUserClient.fetchUser(gameName, tagLine);
		return userAppender.append(user);
	}

}
