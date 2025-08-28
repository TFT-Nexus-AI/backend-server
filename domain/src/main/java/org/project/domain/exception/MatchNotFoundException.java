package org.project.domain.exception;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(String puuid){
        super(String.format("사용자의 전적을 찾을수 없습니다.: %s", puuid));
    }
}
