package org.project.app.match;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchHistoryController {

    private final CollectMatchHistoryUseCase collectMatchHistoryUseCase;

    @PostMapping("/api/matches/by-riot-id")
    public ResponseEntity<CollectMatchHistoryResponse> collectMatchHistory(@RequestBody CollectMatchHistoryRequest request) {
        CollectMatchHistoryResponse response = collectMatchHistoryUseCase.collect(request);
        return ResponseEntity.ok(response);
    }

    public record CollectMatchHistoryRequest(String gameName, String tagLine) {}
    public record CollectMatchHistoryResponse(String message, int savedMatchCount) {}
}
