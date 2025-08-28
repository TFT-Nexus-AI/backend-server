package org.project.domain.user;

import lombok.RequiredArgsConstructor;
import org.project.domain.exception.RiotApiException;
import org.project.domain.exception.UserCreationException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreator {
    private final UserRepository userRepository;
    private final RiotUserClient riotUserClient;

    public User create(String gameName, String tagLine) {
        try {
            User newUser = riotUserClient.getUserFromRiotApi(gameName, tagLine);
            return userRepository.save(newUser);
        } catch (RiotApiException e) {
            throw new UserCreationException("Riot API에서 사용자 정보를 가져올 수 없습니다", e);
        } catch (DataAccessException e) {
            throw new UserCreationException("사용자 저장 중 데이터베이스 오류 발생", e);
        }
    }
}
