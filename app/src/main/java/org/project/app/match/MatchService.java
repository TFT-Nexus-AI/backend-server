package org.project.app.match;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.app.client.RiotApiClient;
import org.project.app.exception.CollectMatchHistoryException;
import org.project.app.exception.RiotApiException;
import org.project.app.exception.UserNotFoundException;
import org.project.app.match.dto.CollectMatchHistoryRequest;
import org.project.app.match.dto.CollectMatchHistoryResponse;
import org.project.app.user.UserService;
import org.project.domain.match.Match;
import org.project.domain.match.MatchRepository;
import org.project.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {

    private final RiotApiClient riotApiClient;
    private final MatchRepository matchRepository;
    private final UserService userService;

    @Transactional
    public CollectMatchHistoryResponse collectMatchHistory(CollectMatchHistoryRequest request) {
        if (!StringUtils.hasText(request.gameName()) || !StringUtils.hasText(request.tagLine())) {
            throw new IllegalArgumentException("gameName과 tagLine은 필수입니다.");
        }

        try {
            RiotApiClient.AccountDto account = riotApiClient.getPuuidByRiotId(request.gameName(), request.tagLine());
            if (account == null) {
                throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
            }

            User user = userService.findByPuuid(account.puuid())
                    .orElseGet(() -> userService.registerUser(account.puuid(), account.gameName(), account.tagLine()));

            List<String> recentMatchIds = riotApiClient.getMatchIdsByPuuid(user.getPuuid());
            List<String> existingMatchIds = matchRepository.findExistingMatchIds(recentMatchIds);

            List<String> newMatchIds = recentMatchIds.stream()
                    .filter(id -> !existingMatchIds.contains(id))
                    .collect(Collectors.toList());

            if (newMatchIds.isEmpty()) {
                return new CollectMatchHistoryResponse("업데이트할 새로운 전적이 없습니다.", 0);
            }

            saveNewMatches(newMatchIds);

            return new CollectMatchHistoryResponse(
                    String.format("새로운 전적 %d건을 성공적으로 저장했습니다.", newMatchIds.size()),
                    newMatchIds.size()
            );
        } catch (RiotApiException e) {
            throw new CollectMatchHistoryException("API 통신 중 오류가 발생했습니다.", e);
        }
    }

    private void saveNewMatches(List<String> matchIds) {
        for (String matchId : matchIds) {
            try {
                RiotApiClient.MatchDto matchDto = riotApiClient.getMatchDetailByMatchId(matchId);
                if (matchDto != null && matchDto.info() != null) {
                    matchRepository.save(Match.builder()
                            .matchId(matchDto.metadata().match_id())
                            .gameDatetime(matchDto.info().game_datetime())
                            .gameLength(matchDto.info().game_length())
                            .gameVersion(matchDto.info().game_version())
                            .tftSet(matchDto.info().tft_set_core_name())
                            .build());
                } else {
                    log.warn("매치 정보를 가져올 수 없습니다: {}", matchId);
                }
            } catch (Exception e) {
                log.error("매치 저장 중 오류 발생: {}", matchId, e);
            }
        }
    }
}