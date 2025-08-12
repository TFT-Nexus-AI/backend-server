package org.project.storage.db.core.match;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "matches")
public class MatchEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, length = 50)
	private String matchId;

	@Column(nullable = false)
	private Long gameDatetime;

	@Column(nullable = false)
	private Float gameLength;

	@Column(nullable = false, length = 20)
	private String gameVersion;

	@Column(nullable = false, length = 50)
	private String tftSet;

	@Builder
	private MatchEntity(String matchId, Long gameDatetime, Float gameLength, String gameVersion, String tftSet) {
		this.matchId = matchId;
		this.gameDatetime = gameDatetime;
		this.gameLength = gameLength;
		this.gameVersion = gameVersion;
		this.tftSet = tftSet;
	}

}
