package org.project.app.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(RiotApiProperties.class)
public class RiotApiClientImpl implements RiotApiClient {

    private final RestTemplate restTemplate;
    private final RiotApiProperties properties;

    @Override
    public RiotUserInfo getUserInfo(String accessToken) {
        // To be implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public AccountDto getPuuidByRiotId(String gameName, String tagLine) {
        String url = properties.url().get("asia") + "/riot/account/v1/accounts/by-riot-id/" + gameName + "/" + tagLine;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", properties.key());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<AccountDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, AccountDto.class);
        System.out.println("riotID"+ response.getBody());
        return response.getBody();
    }

    @Override
    public List<String> getMatchIdsByPuuid(String puuid) {
        String url = properties.url().get("asia") + "/tft/match/v1/matches/by-puuid/" + puuid + "/ids";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", properties.key());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<String>> response = restTemplate.exchange(url, HttpMethod.GET, entity, (Class<List<String>>)(Class<?>)List.class);
        System.out.println("puuid"+ response.getBody());

        return response.getBody();
    }

    @Override
    public MatchDto getMatchDetailByMatchId(String matchId) {
        // To be implemented
        throw new UnsupportedOperationException();
    }
}
