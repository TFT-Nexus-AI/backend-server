package org.project.domain.exception;

public class MatchAlreadyExistsException extends RuntimeException {
    public MatchAlreadyExistsException(String matchId){
        super(String.format("이미 전적이 있습니다 :%s",matchId));
    }
}
