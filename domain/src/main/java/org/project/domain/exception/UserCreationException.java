package org.project.domain.exception;

public class UserCreationException extends RuntimeException {
    public UserCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
