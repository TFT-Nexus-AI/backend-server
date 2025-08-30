package org.project.domain.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String gameName, String tagLine) {
        super(String.format("이미 존재하는 유저입니다.: %s#%s", gameName, tagLine));
    }
}
