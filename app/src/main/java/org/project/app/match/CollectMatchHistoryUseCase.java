package org.project.app.match;

import lombok.RequiredArgsConstructor;
import org.project.app.client.RiotApiClient;
import org.project.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectMatchHistoryUseCase {

    private final RiotApiClient riotApiClient;
    private final UserRepository userRepository; // Will be used later
    // private final MatchRepository matchRepository; // Will be used later

    public MatchHistoryController.CollectMatchHistoryResponse collect(MatchHistoryController.CollectMatchHistoryRequest request) {
        RiotApiClient.AccountDto account = riotApiClient.getPuuidByRiotId(request.gameName(), request.tagLine());
        List<String> matchIds = riotApiClient.getMatchIdsByPuuid(account.puuid());

        // Fake implementation: Assume all matches are new and saved.
        int savedCount = matchIds.size();

        return new MatchHistoryController.CollectMatchHistoryResponse(
                String.format("새로운 전적 %d건을 성공적으로 저장했습니다.", savedCount),
                savedCount
        );
    }
}
