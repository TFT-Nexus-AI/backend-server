package org.project.domain.exception;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(String gameName, String tagLine) {
		super(String.format("사용자를 찾을 수 없습니다: %s#%s", gameName, tagLine));
	}

}
