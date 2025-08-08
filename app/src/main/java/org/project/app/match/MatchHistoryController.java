package org.project.app.match;

import lombok.RequiredArgsConstructor;
import org.project.app.match.dto.CollectMatchHistoryRequest;
import org.project.app.match.dto.CollectMatchHistoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchHistoryController {

    private final MatchService matchService;

    @PostMapping("/api/matches/by-riot-id")
    public ResponseEntity<CollectMatchHistoryResponse> collectMatchHistory(@RequestBody CollectMatchHistoryRequest request) {
        CollectMatchHistoryResponse response = matchService.collectMatchHistory(request);
        return ResponseEntity.ok(response);
    }

}
