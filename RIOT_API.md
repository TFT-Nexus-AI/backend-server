# Riot API Endpoints

이 문서는 TFT-Nexus-AI 프로젝트에서 사용하는 라이엇 게임즈 API의 명세를 정리합니다.

---
## 0. Default Riot API Endpoint
- https://[REGION].api.riotgames.com/
- 각 API 에서 DEFAULT 로 가지는 riot api 지역마다 REGION 이 바뀜
- AMERICAS,ASIA,ESPORTS,ESPORTSEU,EUROPE,SEA resigon **Select Region to Execute Against** 이 각 api 마다 있음
- 각 resion 선택하면 REIGION 부분에 소문자로 치환되어서 들어감

## 1. Account API

### 1.1 PUUID 조회

`gameName`과 `tagLine`으로 사용자의 PUUID를 조회합니다.

- **Endpoint**: `GET /riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}`
- **Select Region to Execute Against**
- **Headers**:
    - `X-Riot-Token`: (Your Riot API Key)
- **Path Parameters**:
    - `gameName` (string): 사용자의 게임 이름 (예: "hide with speard")
    - `tagLine` (string): 사용자의 태그 라인 (예: "6953")
- **Return Value**: `AccountDto` (JSON object containing `puuid`, `gameName`, `tagLine`)

---

## 2. TFT Match API

### 2.1 Match ID 목록 조회

사용자의 PUUID로 최근 경기들의 Match ID 목록을 조회합니다.

- **Endpoint**: `GET /tft/match/v1/matches/by-puuid/{puuid}/ids`
- **Headers**:
    - `X-Riot-Token`: (Your Riot API Key)
- **Path Parameters**:
    - `puuid` (string): 조회할 사용자의 PUUID
- **Query Parameters**:
    - `startTime` (long, optional): 검색 시작 시간 (Unix timestamp)
    - `endTime` (long, optional): 검색 종료 시간 (Unix timestamp)
    - `start` (int, optional, default: 0): 결과 목록의 시작 인덱스
    - `count` (int, optional, default: 20): 반환할 Match ID의 개수
- **Select Region to Execute Against**
- **Return Value**: `List<string>` (Match ID 목록)

### 2.2 Match 상세 정보 조회

Match ID로 해당 경기의 상세 데이터를 조회합니다.

- **Endpoint**: `GET /tft/match/v1/matches/{matchId}`
- **Headers**:
    - `X-Riot-Token`: (Your Riot API Key)
- **Path Parameters**:
    - `matchId` (string): 조회할 경기의 Match ID (예: "KR_1234567890")
- **Return Value**: `MatchDto` (경기 상세 정보가 담긴 복잡한 JSON 객체)

#### `MatchDto`

| Name         | Data Type     | Description     |
|:-------------|:--------------|:----------------|
| **metadata** | `MetadataDto` | Match metadata. |
| **info**     | `InfoDto`     | Match info.     |

#### `MetadataDto`

| Name             | Data Type      | Description                   |
|:-----------------|:---------------|:------------------------------|
| **data_version** | `string`       | Match data version.           |
| **match_id**     | `string`       | Match id.                     |
| **participants** | `List<string>` | A list of participant PUUIDs. |

#### `InfoDto`

| Name                | Data Type              | Description                                                     |
|:--------------------|:-----------------------|:----------------------------------------------------------------|
| **endOfGameResult** | `string`               | Refer to indicate if the game ended in termination.             |
| **gameCreation**    | `long`                 | Unix timestamp for when the game is created on the game server. |
| **game_datetime**   | `long`                 | Unix timestamp.                                                 |
| **game_length**     | `float`                | Game length in seconds.                                         |
| **game_version**    | `string`               | Game client version.                                            |
| **participants**    | `List<ParticipantDto>` | List of participants in the match.                              |
| **queue_id**        | `int`                  | Please refer to the League of Legends documentation.            |
| **tft_game_type**   | `string`               | Teamfight Tactics game type.                                    |
| **tft_set_number**  | `int`                  | Teamfight Tactics set number.                                   |

#### `ParticipantDto`

| Name                        | Data Type        | Description                                                   |
|:----------------------------|:-----------------|:--------------------------------------------------------------|
| **companion**               | `CompanionDto`   | Participant's companion.                                      |
| **gold_left**               | `int`            | Gold left after participant was eliminated.                   |
| **last_round**              | `int`            | The round the participant was eliminated in.                  |
| **level**                   | `int`            | Participant Little Legend level.                              |
| **placement**               | `int`            | Participant placement upon elimination.                       |
| **players_eliminated**      | `int`            | Number of players the participant eliminated.                 |
| **puuid**                   | `string`         |                                                               |
| **time_eliminated**         | `float`          | The number of seconds before the participant was eliminated.  |
| **total_damage_to_players** | `int`            | Damage the participant dealt to other players.                |
| **traits**                  | `List<TraitDto>` | A complete list of traits for the participant's active units. |
| **units**                   | `List<UnitDto>`  | A list of active units for the participant.                   |

#### `CompanionDto`

| Name           | Data Type | Description |
|:---------------|:----------|:------------|
| **content_ID** | `string`  |             |
| **item_ID**    | `int`     |             |
| **skin_ID**    | `int`     |             |
| **species**    | `string`  |             |

#### `TraitDto`

| Name             | Data Type | Description                                                                         |
|:-----------------|:----------|:------------------------------------------------------------------------------------|
| **name**         | `string`  | Trait name.                                                                         |
| **num_units**    | `int`     | Number of units with this trait.                                                    |
| **style**        | `int`     | Current style for this trait. (0=No style, 1=Bronze, 2=Silver, 3=Gold, 4=Chromatic) |
| **tier_current** | `int`     | Current active tier for the trait.                                                  |

#### `UnitDto`

| Name             | Data Type   | Description                                                  |
|:-----------------|:------------|:-------------------------------------------------------------|
| **items**        | `List<int>` | A list of the unit's items.                                  |
| **character_id** | `string`    | This field was introduced in patch 9.22 with data_version 2. |
| **rarity**       | `int`       | Unit rarity. This doesn't equate to the unit cost.           |
| **tier**         | `int`       | Unit tier.                                                   |

---

## 3. TFT Summoner API

### 3.1 Summoner 조회

PUUID로 소환사 정보를 조회합니다.

- **Endpoint**: `GET /tft/summoner/v1/summoners/by-puuid/{encryptedPUUID}`
- **Headers**:
    - `X-Riot-Token`: (Your Riot API Key)
- **Path Parameters**:
    - `encryptedPUUID` (string): 조회할 사용자의 암호화된 PUUID
- **Return Value**: `SummonerDto`

#### `SummonerDto`

| Name              | Data Type | Description                                                      |
|:------------------|:----------|:-----------------------------------------------------------------|
| **puuid**         | `string`  | Encrypted PUUID. Exact length of 78 characters.                  |
| **profileIconId** | `int`     | ID of the summoner icon associated with the summoner.            |
| **revisionDate**  | `long`    | Date summoner was last modified specified as epoch milliseconds. |
| **summonerLevel** | `long`    | Summoner level associated with the summoner.                     |
