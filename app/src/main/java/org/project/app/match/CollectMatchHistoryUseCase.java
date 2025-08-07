package org.project.app.match;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.app.client.RiotApiClient;
import org.project.app.exception.CollectMatchHistoryException;
import org.project.app.exception.RiotApiException;
import org.project.app.exception.UserNotFoundException;
import org.project.domain.match.Match;
import org.project.domain.match.MatchRepository;
import org.project.domain.user.User;
import org.project.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectMatchHistoryUseCase {

    private final RiotApiClient riotApiClient;
    private final UserRepository userRepository; // Will be used later
    private final MatchRepository matchRepository;

    public MatchHistoryController.CollectMatchHistoryResponse collect(MatchHistoryController.CollectMatchHistoryRequest request) {
        if (!StringUtils.hasText(request.gameName()) || !StringUtils.hasText(request.tagLine())) {
            throw new IllegalArgumentException("gameName과 tagLine은 필수입니다.");
        }

        try {
            RiotApiClient.AccountDto account = riotApiClient.getPuuidByRiotId(request.gameName(), request.tagLine());
            if (account == null) {
                throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
            }

            log.info("Attempting to find or create user with puuid: {}", account.puuid());
            User user = userRepository.findByPuuid(account.puuid())
                    .orElseGet(() -> {
                        log.info("User not found, creating new user with puuid: {}", account.puuid());
                        return userRepository.save(User.create(account.puuid(), account.gameName(), account.tagLine()));
                    });
            log.info("User found/created: {}", user.getPuuid());

            // 2. Filter out existing matches
            log.info("Fetching recent match IDs for puuid: {}", user.getPuuid());
            List<String> recentMatchIds = riotApiClient.getMatchIdsByPuuid(user.getPuuid());
            log.info("Recent match IDs from Riot API: {}", recentMatchIds);

            log.info("Finding existing match IDs in DB from: {}", recentMatchIds);
            List<String> existingMatchIds = matchRepository.findExistingMatchIds(recentMatchIds);
            log.info("Existing match IDs in DB: {}", existingMatchIds);

            List<String> newMatchIds = recentMatchIds.stream()
                    .filter(id -> !existingMatchIds.contains(id))
                    .collect(Collectors.toList());
            log.info("New match IDs to save: {}", newMatchIds);

            if (newMatchIds.isEmpty()) {
                log.info("No new matches to update.");
                return new MatchHistoryController.CollectMatchHistoryResponse("업데이트할 새로운 전적이 없습니다.", 0);
            }

            // 3. Save new matches
            log.info("Saving {} new matches.", newMatchIds.size());
            for (String matchId : newMatchIds) {
                matchRepository.save(Match.builder().matchId(matchId).build());
                log.debug("Saved match: {}", matchId);
            }

            log.info("Successfully saved {} new matches.", newMatchIds.size());
            return new MatchHistoryController.CollectMatchHistoryResponse(
                    String.format("새로운 전적 %d건을 성공적으로 저장했습니다.", newMatchIds.size()),
                    newMatchIds.size()
            );
        } catch (RiotApiException e) {
            throw new CollectMatchHistoryException("API 통신 중 오류가 발생했습니다.", e);
        }
    }
}
