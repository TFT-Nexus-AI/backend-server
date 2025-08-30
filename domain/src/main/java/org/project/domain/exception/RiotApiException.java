package org.project.domain.exception;

public class RiotApiException extends RuntimeException {

	public RiotApiException() {
		super("Riot API 호출 중 오류가 발생했습니다");
	}

	public RiotApiException(String message) {
		super(message);
	}

	public RiotApiException(String message, Throwable cause) {
		super(message, cause);
	}

}