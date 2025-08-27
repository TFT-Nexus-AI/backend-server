package org.project.app.exception;

public class RiotApiException extends RuntimeException {
    public RiotApiException(String message) {
        super(message);
    }
}