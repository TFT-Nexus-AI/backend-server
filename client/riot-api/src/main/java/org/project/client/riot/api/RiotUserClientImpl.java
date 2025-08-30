package org.project.client.riot.api;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.project.client.riot.api.config.RiotApiProperties;
import org.project.domain.user.RiotUserClient;

import org.project.domain.user.SummonerInfo;
import org.project.domain.user.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiotUserClientImpl implements RiotUserClient {

	private final RiotWebClient webClient;

	private RiotApiProperties properties;

	@Override
	public User fetchUser(String gameName, String tagLine) {

		Region accountRegion = getAccountRegion();

		AccountDto dto = webClient.getAccount(gameName, tagLine, accountRegion);
		return User.create(dto.puuid(), dto.gameName(), dto.tagLine());

	}

	@Override
	public SummonerInfo fetchSummoner(String puuid) {
		Region summonerRegion = getSummonerRegion();
		SummonerInfoDto dto = webClient.getSummoner(puuid, summonerRegion);
		return SummonerInfo.create(dto.id(), dto.accountId(), dto.puuid(), dto.profileIconId(), dto.summonerLevel());
	}

	/**
	 * Account API용 Region 결정 Account API는 대륙별 글로벌 엔드포인트 사용
	 */
	private Region getAccountRegion() {
		// 설정에서 기본 지역을 읽어오거나,
		// 사용자의 위치에 따라 동적으로 결정 가능
		String defaultRegion = properties.defaultRegion();

		return switch (defaultRegion.toUpperCase()) {
			case "KR", "JP" -> Region.ASIA;
			case "BR", "LAN", "LAS", "NA" -> Region.AMERICAS;
			case "EUW", "EUNE", "TR", "RU" -> Region.EUROPE;
			default -> Region.ASIA; // 기본값
		};
	}

	/**
	 * Summoner/Match API용 Region 결정 지역별 구체적인 엔드포인트 사용
	 */
	private Region getSummonerRegion() {
		String defaultRegion = properties.defaultRegion();

		try {
			return Region.valueOf(defaultRegion.toUpperCase());
		}
		catch (IllegalArgumentException e) {
			log.warn("Invalid region: {}, using default KR", defaultRegion);
			return Region.KR;
		}
	}

}
