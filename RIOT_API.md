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

### **Return Value: `MatchDto`**

| Name | Data Type | Description |
| :--- | :--- | :--- |
| `metadata` | `MetadataDto` | 매치 메타데이터입니다. |
| `info` | `InfoDto` | 매치 정보입니다. |


### **`MetadataDto`**

| Name | Data Type | Description |
| :--- | :--- | :--- |
| `data_version` | `string` | 매치 데이터 버전입니다. |
| `match_id` | `string` | 매치 ID입니다. |
| `participants` | `List[string]` | 참여자들의 PUUID 목록입니다. |

\<br\>

### **`InfoDto`**

| Name | Data Type | Description |
| :--- | :--- | :--- |
| `endOfGameResult` | `string` | 게임이 비정상적으로 종료되었는지 여부를 나타냅니다. |
| `gameCreation` | `long` | 게임이 생성된 시점의 Unix 타임스탬프입니다 (로딩 화면 기준). |
| `gameId` | `long` | 게임 ID입니다. |
| `game_datetime` | `long` | Unix 타임스탬프입니다. |
| `game_length` | `float` | 게임 시간 (초)입니다. |
| `game_version` | `string` | 게임 클라이언트 버전입니다. |
| `mapId` | `int` | Game Constants 문서를 참고하세요. |
| `participants` | `List[ParticipantDto]` | 참여자 정보 목록입니다. |
| `queue_id` | `int` | League of Legends 문서를 참고하세요. |
| `tft_game_type` | `string` | 전략적 팀 전투 게임 유형입니다. |
| `tft_set_core_name` | `string` | 전략적 팀 전투 게임 세트 이름입니다. |
| `tft_set_number` | `int` | 전략적 팀 전투 세트 번호입니다. |
| `game_variation` | `string` | **Deprecated.** TFT static data에 문서화된 게임 변형 키입니다. |
| `queueId` | `int` | **Deprecated.** `queue_id`를 대신 사용하세요. |

\<br\>

### **`ParticipantDto`**

| Name | Data Type | Description |
| :--- | :--- | :--- |
| `puuid` | `string` | |
| `riotIdGameName` | `string` | |
| `riotIdTagline` | `string` | |
| `placement` | `int` | 참여자의 최종 순위입니다. |
| `last_round` | `int` | 참여자가 탈락한 라운드입니다. (예: 2-1 스테이지에서 탈락 시 5) |
| `level` | `int` | 참여자의 꼬마 전설이 레벨입니다. |
| `players_eliminated` | `int` | 해당 참여자가 탈락시킨 플레이어 수입니다. |
| `time_eliminated` | `float` | 참여자가 탈락하기까지 걸린 시간(초)입니다. |
| `gold_left` | `int` | 참여자가 탈락했을 때 남은 골드입니다. |
| `total_damage_to_players` | `int` | 다른 플레이어에게 입힌 총 피해량입니다. |
| `traits` | `List[TraitDto]` | 활성화된 특성의 전체 목록입니다. |
| `units` | `List[UnitDto]` | 활성화된 유닛의 목록입니다. |
| `companion` | `CompanionDto` | 참여자의 꼬마 전설이 정보입니다. |
| `missions` | `MissionDto` | **(JSON 응답 기반 추가)** 미션 달성 정보입니다. |
| `win` | `boolean` | **(JSON 응답 기반 추가)** 승리 여부입니다. |

\<br\>

### **`CompanionDto`**

| Name | Data Type | Description |
| :--- | :--- | :--- |
| `content_ID` | `string` | |
| `item_ID` | `int` | |
| `skin_ID` | `int` | |
| `species` | `string` | |

\<br\>

### **`TraitDto`**

| Name | Data Type | Description |
| :--- | :--- | :--- |
| `name` | `string` | 특성 이름입니다. |
| `num_units` | `int` | 이 특성을 가진 유닛의 수입니다. |
| `style` | `int` | 현재 특성의 스타일입니다. (0: No style, 1: Bronze, 2: Silver, 3: Gold, 4: Chromatic) |
| `tier_current` | `int` | 현재 활성화된 특성 등급입니다. |
| `tier_total` | `int` | 해당 특성의 전체 등급 수입니다. |

\<br\>

### **`UnitDto`**

| Name | Data Type | Description |
| :--- | :--- | :--- |
| `character_id` | `string` | 9.22 패치, 데이터 버전 2에서 도입되었습니다. |
| `name` | `string` | 유닛 이름입니다. 종종 비어 있습니다. |
| `rarity` | `int` | 유닛 희귀도입니다. 유닛 비용과 동일하지 않습니다. |
| `tier` | `int` | 유닛 등급(성)입니다. |
| `items` | `List[int]` | 유닛의 아이템 목록입니다. 아이템 ID는 TFT 문서를 참고하세요. |
| `itemNames` | `List[string]` | **(JSON 응답 기반 추가)** 유닛이 장착한 아이템의 이름 목록입니다. |
| `chosen` | `string` | 운명(Fates) 세트 메카닉의 일부로 선택받은 자 유닛일 경우, 선택된 특성이 이 필드에 표시됩니다. |

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











