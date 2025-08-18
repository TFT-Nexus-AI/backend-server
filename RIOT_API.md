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

<br>

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

<br>

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

<br>

### **`CompanionDto`**

| Name | Data Type | Description |
| :--- | :--- | :--- |
| `content_ID` | `string` | |
| `item_ID` | `int` | |
| `skin_ID` | `int` | |
| `species` | `string` | |

<br>

### **`TraitDto`**

| Name | Data Type | Description |
| :--- | :--- | :--- |
| `name` | `string` | 특성 이름입니다. |
| `num_units` | `int` | 이 특성을 가진 유닛의 수입니다. |
| `style` | `int` | 현재 특성의 스타일입니다. (0: No style, 1: Bronze, 2: Silver, 3: Gold, 4: Chromatic) |
| `tier_current` | `int` | 현재 활성화된 특성 등급입니다. |
| `tier_total` | `int` | 해당 특성의 전체 등급 수입니다. |

<br>

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
### **Example Response**
```json
{
    "metadata": {
        "data_version": "6",
        "match_id": "KR_7768148854",
        "participants": [
            "0PuYrQ7PAEcp9QqE3hXGqLClKpR8IMQK0qf8KhNOkee6bY0v7w0n8Ty-SSrZ-p_IfQ5mN1ioWd8ZIQ",
            "4gWssIFhm-hNikQhs-BhXZQVMcD2uHy0NntcLrlWPza3dMmeYYlnMI7LW9fRtuZIk7pNhoDyXnYMVA",
            "gyQmw7OhODvS9BbB_7qiJxlfagb8ijqWDVu5Tm1Thbb7cXtIkDl8vaiAkIIHIZfBk1bIJAv4OtMINQ",
            "9rQ2HaEakGI8o4LT5gjQM8nA-_V1GUqeHB0oZUQmPsqBJJOC8cdhKKVzLWUytAS7PEdjSk5RcVFjpQ",
            "gLAfMGlYqVOgUuJfHCN1pWRJpyTwgP5MYKsfa7XvauORjLgi43lC6YEFf00lVcHqgF6cik2PTjlTIA",
            "D5tqQ52QQAbenfbBVKGetOpP42yg5P13Z4FAx2FDA05HEAETWT37XPJKedwMKSkXLlwd9o3NwCUq8A",
            "9jMZQuhvx-X2UzGRTalvD3uYMojAoFIJVb88aArGli1FJIQyN3p7PrXTeA_oNcrcNcASh5lg7W1ANg",
            "r_UZeJs33_LW0ERBBzMR4PS0Uh9y-ooClfTkzn7bb20pNavMJEtF_agMAKVr50cN4TGKDxsuKhgtDA"
        ]
    },
    "info": {
        "endOfGameResult": "GameComplete",
        "gameCreation": 1755487170000,
        "gameId": 7768148854,
        "game_datetime": 1755489434093,
        "game_length": 2244.1083984375,
        "game_version": "Linux Version 15.16.704.6097 (Aug 14 2025/15:17:38) [PUBLIC] ",
        "mapId": 22,
        "participants": [
            {
                "companion": {
                    "content_ID": "0f2dd6dd-2240-4e60-8aa3-6de3ba7ceec2",
                    "item_ID": 64001,
                    "skin_ID": 1,
                    "species": "PetChibiTeemo"
                },
                "gold_left": 50,
                "last_round": 26,
                "level": 7,
                "missions": {
                    "PlayerScore2": 79
                },
                "placement": 6,
                "players_eliminated": 0,
                "puuid": "0PuYrQ7PAEcp9QqE3hXGqLClKpR8IMQK0qf8KhNOkee6bY0v7w0n8Ty-SSrZ-p_IfQ5mN1ioWd8ZIQ",
                "riotIdGameName": "K사원",
                "riotIdTagline": "7924",
                "time_eliminated": 1588.7642822265625,
                "total_damage_to_players": 48,
                "traits": [
                    {
                        "name": "TFT15_Duelist",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Empyrean",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Juggernaut",
                        "num_units": 5,
                        "style": 2,
                        "tier_current": 2,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Luchador",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 2
                    },
                    {
                        "name": "TFT15_OldMentor",
                        "num_units": 1,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Sniper",
                        "num_units": 3,
                        "style": 2,
                        "tier_current": 2,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_SoulFighter",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 5
                    },
                    {
                        "name": "TFT15_StarGuardian",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 12
                    }
                ],
                "units": [
                    {
                        "character_id": "TFT15_Naafiri",
                        "itemNames": [],
                        "name": "",
                        "rarity": 0,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Gnar",
                        "itemNames": [],
                        "name": "",
                        "rarity": 0,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Jhin",
                        "itemNames": [
                            "TFT_Item_Bloodthirster",
                            "TFT_Item_Artifact_Fishbones",
                            "TFT15_Item_JuggernautEmblemItem"
                        ],
                        "name": "",
                        "rarity": 1,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_DrMundo",
                        "itemNames": [
                            "TFT_Item_Redemption",
                            "TFT_Item_GargoyleStoneplate",
                            "TFT_Item_SpectralGauntlet"
                        ],
                        "name": "",
                        "rarity": 1,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_Udyr",
                        "itemNames": [],
                        "name": "",
                        "rarity": 2,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Jinx",
                        "itemNames": [
                            "TFT_Item_GuinsoosRageblade",
                            "TFT_Item_InfinityEdge"
                        ],
                        "name": "",
                        "rarity": 4,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_Sett",
                        "itemNames": [
                            "TFT_Item_RedBuff"
                        ],
                        "name": "",
                        "rarity": 4,
                        "tier": 1
                    }
                ],
                "win": false
            },
            {
                "companion": {
                    "content_ID": "4af50f78-340d-4ffb-bc60-551fdad25aa2",
                    "item_ID": 66010,
                    "skin_ID": 10,
                    "species": "PetPoro"
                },
                "gold_left": 134,
                "last_round": 30,
                "level": 6,
                "missions": {
                    "PlayerScore2": 91
                },
                "placement": 5,
                "players_eliminated": 0,
                "puuid": "4gWssIFhm-hNikQhs-BhXZQVMcD2uHy0NntcLrlWPza3dMmeYYlnMI7LW9fRtuZIk7pNhoDyXnYMVA",
                "riotIdGameName": "Aatrox할랭",
                "riotIdTagline": "KR1",
                "time_eliminated": 1811.0428466796875,
                "total_damage_to_players": 59,
                "traits": [
                    {
                        "name": "TFT15_Duelist",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Empyrean",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Heavyweight",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Juggernaut",
                        "num_units": 4,
                        "style": 2,
                        "tier_current": 2,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Luchador",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 2
                    },
                    {
                        "name": "TFT15_OldMentor",
                        "num_units": 1,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_SentaiRanger",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_SoulFighter",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 5
                    }
                ],
                "units": [
                    {
                        "character_id": "TFT15_Kayle",
                        "itemNames": [
                            "TFT_Item_GuinsoosRageblade",
                            "TFT_Item_PowerGauntlet",
                            "TFT_Item_SparringGloves"
                        ],
                        "name": "",
                        "rarity": 0,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_Naafiri",
                        "itemNames": [],
                        "name": "",
                        "rarity": 0,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Zac",
                        "itemNames": [
                            "TFT_Item_GargoyleStoneplate",
                            "TFT_Item_WarmogsArmor",
                            "TFT_Item_Redemption"
                        ],
                        "name": "",
                        "rarity": 0,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_Aatrox",
                        "itemNames": [
                            "TFT_Item_NightHarvester"
                        ],
                        "name": "",
                        "rarity": 0,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_DrMundo",
                        "itemNames": [],
                        "name": "",
                        "rarity": 1,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Udyr",
                        "itemNames": [],
                        "name": "",
                        "rarity": 2,
                        "tier": 2
                    }
                ],
                "win": false
            },
            {
                "companion": {
                    "content_ID": "b4f1a74a-9391-4c4b-931d-bbaf2d15f43b",
                    "item_ID": 120002,
                    "skin_ID": 2,
                    "species": "PetChibiLillia"
                },
                "gold_left": 51,
                "last_round": 15,
                "level": 6,
                "missions": {
                    "PlayerScore2": 43
                },
                "placement": 8,
                "players_eliminated": 0,
                "puuid": "gyQmw7OhODvS9BbB_7qiJxlfagb8ijqWDVu5Tm1Thbb7cXtIkDl8vaiAkIIHIZfBk1bIJAv4OtMINQ",
                "riotIdGameName": "혀나뿌뿌",
                "riotIdTagline": "5444",
                "time_eliminated": 864.6364135742188,
                "total_damage_to_players": 12,
                "traits": [
                    {
                        "name": "TFT15_BattleAcademia",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_GemForce",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_Protector",
                        "num_units": 5,
                        "style": 2,
                        "tier_current": 2,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Spellslinger",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_StarGuardian",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 12
                    },
                    {
                        "name": "TFT15_Strategist",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_SupremeCells",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_TheCrew",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 7
                    }
                ],
                "units": [
                    {
                        "character_id": "TFT15_Kennen",
                        "itemNames": [],
                        "name": "",
                        "rarity": 0,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_Malphite",
                        "itemNames": [],
                        "name": "",
                        "rarity": 0,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_Rakan",
                        "itemNames": [],
                        "name": "",
                        "rarity": 1,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_Janna",
                        "itemNames": [
                            "TFT_Item_GuinsoosRageblade",
                            "TFT_Item_TearOfTheGoddess"
                        ],
                        "name": "",
                        "rarity": 1,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_Ahri",
                        "itemNames": [],
                        "name": "",
                        "rarity": 2,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_Neeko",
                        "itemNames": [
                            "TFT_Item_AdaptiveHelm",
                            "TFT_Item_AdaptiveHelm"
                        ],
                        "name": "",
                        "rarity": 2,
                        "tier": 1
                    }
                ],
                "win": false
            },
            {
                "companion": {
                    "content_ID": "67b2e7fb-8402-4fcf-8410-d5cbc431a11a",
                    "item_ID": 14028,
                    "skin_ID": 28,
                    "species": "PetQiyanaDog"
                },
                "gold_left": 3,
                "last_round": 37,
                "level": 8,
                "missions": {
                    "PlayerScore2": 223
                },
                "placement": 2,
                "players_eliminated": 0,
                "puuid": "9rQ2HaEakGI8o4LT5gjQM8nA-_V1GUqeHB0oZUQmPsqBJJOC8cdhKKVzLWUytAS7PEdjSk5RcVFjpQ",
                "riotIdGameName": "롤하는 율리",
                "riotIdTagline": "YUL",
                "time_eliminated": 2229.738037109375,
                "total_damage_to_players": 131,
                "traits": [
                    {
                        "name": "TFT15_Bastion",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_BattleAcademia",
                        "num_units": 7,
                        "style": 4,
                        "tier_current": 3,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_Heavyweight",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_OldMentor",
                        "num_units": 1,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Prodigy",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_Protector",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Sniper",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    }
                ],
                "units": [
                    {
                        "character_id": "TFT15_Ezreal",
                        "itemNames": [
                            "TFT_Item_InfinityEdge",
                            "TFT_Item_Deathblade",
                            "TFT_Item_HextechGunblade"
                        ],
                        "name": "",
                        "rarity": 0,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_Garen",
                        "itemNames": [
                            "TFT_Item_GargoyleStoneplate",
                            "TFT_Item_GiantsBelt"
                        ],
                        "name": "",
                        "rarity": 0,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_Rakan",
                        "itemNames": [],
                        "name": "",
                        "rarity": 1,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Kobuko",
                        "itemNames": [
                            "TFT_Item_ThiefsGloves",
                            "TFT_Item_Crownguard",
                            "TFT_Item_RabadonsDeathcap"
                        ],
                        "name": "",
                        "rarity": 1,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Caitlyn",
                        "itemNames": [
                            "TFT_Item_SpearOfShojin",
                            "TFT_Item_InfinityEdge",
                            "TFT_Item_GuinsoosRageblade"
                        ],
                        "name": "",
                        "rarity": 2,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_Jayce",
                        "itemNames": [
                            "TFT_Item_SteraksGage",
                            "TFT_Item_Bloodthirster"
                        ],
                        "name": "",
                        "rarity": 2,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Leona",
                        "itemNames": [
                            "TFT_Item_FrozenHeart",
                            "TFT_Item_SpectralGauntlet",
                            "TFT_Item_RedBuff"
                        ],
                        "name": "",
                        "rarity": 4,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Yuumi",
                        "itemNames": [],
                        "name": "",
                        "rarity": 4,
                        "tier": 2
                    }
                ],
                "win": true
            },
            {
                "companion": {
                    "content_ID": "136360d3-60da-407d-898f-e1bdd5ea0adf",
                    "item_ID": 127004,
                    "skin_ID": 4,
                    "species": "PetPowderMonkey"
                },
                "gold_left": 19,
                "last_round": 33,
                "level": 8,
                "missions": {
                    "PlayerScore2": 198
                },
                "placement": 4,
                "players_eliminated": 0,
                "puuid": "gLAfMGlYqVOgUuJfHCN1pWRJpyTwgP5MYKsfa7XvauORjLgi43lC6YEFf00lVcHqgF6cik2PTjlTIA",
                "riotIdGameName": "아관심좀받고싶다",
                "riotIdTagline": "KR1",
                "time_eliminated": 1989.858154296875,
                "total_damage_to_players": 100,
                "traits": [
                    {
                        "name": "TFT15_Bastion",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Captain",
                        "num_units": 1,
                        "style": 3,
                        "tier_current": 1,
                        "tier_total": 1
                    },
                    {
                        "name": "TFT15_Edgelord",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_GemForce",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_MonsterTrainer",
                        "num_units": 1,
                        "style": 3,
                        "tier_current": 1,
                        "tier_total": 1
                    },
                    {
                        "name": "TFT15_Protector",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_SentaiRanger",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_Sniper",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_Strategist",
                        "num_units": 3,
                        "style": 2,
                        "tier_current": 2,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_TheCrew",
                        "num_units": 5,
                        "style": 1,
                        "tier_current": 7,
                        "tier_total": 7
                    }
                ],
                "units": [
                    {
                        "character_id": "TFT15_Sivir",
                        "itemNames": [
                            "TFT_Item_SpearOfShojin",
                            "TFT_Item_MadredsBloodrazor"
                        ],
                        "name": "",
                        "rarity": 0,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_Malphite",
                        "itemNames": [
                            "TFT_Item_ThiefsGloves",
                            "TFT_Item_StatikkShiv",
                            "TFT_Item_RapidFireCannon"
                        ],
                        "name": "",
                        "rarity": 0,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_Shen",
                        "itemNames": [
                            "TFT_Item_ThiefsGloves",
                            "TFT_Item_StatikkShiv",
                            "TFT_Item_RapidFireCannon"
                        ],
                        "name": "",
                        "rarity": 1,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_Janna",
                        "itemNames": [],
                        "name": "",
                        "rarity": 1,
                        "tier": 1
                    },
                    {
                        "character_id": "tft15_rammus",
                        "itemNames": [
                            "TFT_Item_Redemption",
                            "TFT_Item_GargoyleStoneplate",
                            "TFT_Item_NightHarvester"
                        ],
                        "name": "",
                        "rarity": 2,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Ziggs",
                        "itemNames": [
                            "TFT_Item_RabadonsDeathcap",
                            "TFT_Item_Leviathan",
                            "TFT_Item_SpearOfShojin"
                        ],
                        "name": "",
                        "rarity": 2,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_JarvanIV",
                        "itemNames": [
                            "TFT_Item_ThiefsGloves",
                            "TFT_Item_RedBuff",
                            "TFT_Item_GargoyleStoneplate"
                        ],
                        "name": "",
                        "rarity": 4,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_TwistedFate",
                        "itemNames": [
                            "TFT_Item_StatikkShiv",
                            "TFT_Item_GuinsoosRageblade",
                            "TFT_Item_JeweledGauntlet"
                        ],
                        "name": "",
                        "rarity": 6,
                        "tier": 1
                    }
                ],
                "win": true
            },
            {
                "companion": {
                    "content_ID": "e69f4403-c591-47c0-8cb2-4693b52ff076",
                    "item_ID": 107002,
                    "skin_ID": 2,
                    "species": "PetStyleTwoJhin"
                },
                "gold_left": 0,
                "last_round": 37,
                "level": 8,
                "missions": {
                    "PlayerScore2": 223
                },
                "placement": 3,
                "players_eliminated": 2,
                "puuid": "D5tqQ52QQAbenfbBVKGetOpP42yg5P13Z4FAx2FDA05HEAETWT37XPJKedwMKSkXLlwd9o3NwCUq8A",
                "riotIdGameName": "과학오디세이아",
                "riotIdTagline": "KR2",
                "time_eliminated": 2219.92236328125,
                "total_damage_to_players": 150,
                "traits": [
                    {
                        "name": "TFT15_Bastion",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_BattleAcademia",
                        "num_units": 7,
                        "style": 4,
                        "tier_current": 3,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_Destroyer",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_OldMentor",
                        "num_units": 1,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Prodigy",
                        "num_units": 3,
                        "style": 2,
                        "tier_current": 2,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_Protector",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_StarGuardian",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 12
                    },
                    {
                        "name": "TFT15_Strategist",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    }
                ],
                "units": [
                    {
                        "character_id": "TFT15_Ezreal",
                        "itemNames": [],
                        "name": "",
                        "rarity": 0,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Garen",
                        "itemNames": [],
                        "name": "",
                        "rarity": 0,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Rakan",
                        "itemNames": [
                            "TFT_Item_AdaptiveHelm",
                            "TFT_Item_BrambleVest"
                        ],
                        "name": "",
                        "rarity": 1,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_Katarina",
                        "itemNames": [
                            "TFT_Item_UnstableConcoction",
                            "TFT_Item_GuardianAngel",
                            "TFT_Item_JeweledGauntlet"
                        ],
                        "name": "",
                        "rarity": 1,
                        "tier": 3
                    },
                    {
                        "character_id": "TFT15_Leona",
                        "itemNames": [
                            "TFT_Item_IonicSpark",
                            "TFT_Item_WarmogsArmor",
                            "TFT_Item_GargoyleStoneplate"
                        ],
                        "name": "",
                        "rarity": 4,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Ryze",
                        "itemNames": [],
                        "name": "",
                        "rarity": 4,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_Yuumi",
                        "itemNames": [
                            "TFT_Item_JeweledGauntlet",
                            "TFT_Item_BlueBuff",
                            "TFT_Item_RapidFireCannon"
                        ],
                        "name": "",
                        "rarity": 4,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Seraphine",
                        "itemNames": [
                            "TFT_Item_BlueBuff",
                            "TFT15_Item_BattleAcademiaEmblemItem",
                            "TFT_Item_RabadonsDeathcap"
                        ],
                        "name": "",
                        "rarity": 6,
                        "tier": 2
                    }
                ],
                "win": true
            },
            {
                "companion": {
                    "content_ID": "136360d3-60da-407d-898f-e1bdd5ea0adf",
                    "item_ID": 127004,
                    "skin_ID": 4,
                    "species": "PetPowderMonkey"
                },
                "gold_left": 24,
                "last_round": 26,
                "level": 8,
                "missions": {
                    "PlayerScore2": 79
                },
                "placement": 7,
                "players_eliminated": 0,
                "puuid": "9jMZQuhvx-X2UzGRTalvD3uYMojAoFIJVb88aArGli1FJIQyN3p7PrXTeA_oNcrcNcASh5lg7W1ANg",
                "riotIdGameName": "뿌꾸뿌꾸뾰로롱",
                "riotIdTagline": "KR1",
                "time_eliminated": 1584.658447265625,
                "total_damage_to_players": 27,
                "traits": [
                    {
                        "name": "TFT15_Bastion",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Destroyer",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_Duelist",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Edgelord",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Empyrean",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Juggernaut",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Sniper",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_SoulFighter",
                        "num_units": 7,
                        "style": 2,
                        "tier_current": 3,
                        "tier_total": 5
                    },
                    {
                        "name": "TFT15_Spellslinger",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    }
                ],
                "units": [
                    {
                        "character_id": "TFT15_Kalista",
                        "itemNames": [
                            "TFT_Item_ChainVest"
                        ],
                        "name": "",
                        "rarity": 0,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Naafiri",
                        "itemNames": [
                            "TFT_Item_FryingPan"
                        ],
                        "name": "",
                        "rarity": 0,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Lux",
                        "itemNames": [
                            "TFT_Item_JeweledGauntlet",
                            "TFT_Item_Morellonomicon"
                        ],
                        "name": "",
                        "rarity": 1,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_XinZhao",
                        "itemNames": [
                            "TFT9_Item_OrnnHullbreaker",
                            "TFT9_Item_OrnnHullbreaker",
                            "TFT9_Item_OrnnHullbreaker"
                        ],
                        "name": "",
                        "rarity": 1,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Viego",
                        "itemNames": [
                            "TFT_Item_TitansResolve",
                            "TFT_Item_DragonsClaw"
                        ],
                        "name": "",
                        "rarity": 2,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Samira",
                        "itemNames": [
                            "TFT_Item_SpearOfShojin",
                            "TFT_Item_LastWhisper"
                        ],
                        "name": "",
                        "rarity": 4,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_Sett",
                        "itemNames": [],
                        "name": "",
                        "rarity": 4,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_Varus",
                        "itemNames": [
                            "TFT_Item_LastWhisper",
                            "TFT4_Item_OrnnTheCollector",
                            "TFT_Item_Deathblade"
                        ],
                        "name": "",
                        "rarity": 6,
                        "tier": 1
                    }
                ],
                "win": false
            },
            {
                "companion": {
                    "content_ID": "fe15dc56-e159-4ee3-9c0e-a5f0679e6aae",
                    "item_ID": 25017,
                    "skin_ID": 17,
                    "species": "PetAoShin"
                },
                "gold_left": 2,
                "last_round": 37,
                "level": 9,
                "missions": {
                    "PlayerScore2": 223
                },
                "placement": 1,
                "players_eliminated": 2,
                "puuid": "r_UZeJs33_LW0ERBBzMR4PS0Uh9y-ooClfTkzn7bb20pNavMJEtF_agMAKVr50cN4TGKDxsuKhgtDA",
                "riotIdGameName": "옹 담",
                "riotIdTagline": "KR1",
                "time_eliminated": 2230.939697265625,
                "total_damage_to_players": 161,
                "traits": [
                    {
                        "name": "TFT15_BattleAcademia",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_Destroyer",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_Empyrean",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_GemForce",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_OldMentor",
                        "num_units": 1,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Protector",
                        "num_units": 6,
                        "style": 4,
                        "tier_current": 3,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_Sniper",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_Spellslinger",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_StarGuardian",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 12
                    },
                    {
                        "name": "TFT15_Strategist",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 4
                    },
                    {
                        "name": "TFT15_SupremeCells",
                        "num_units": 2,
                        "style": 1,
                        "tier_current": 1,
                        "tier_total": 3
                    },
                    {
                        "name": "TFT15_TheCrew",
                        "num_units": 1,
                        "style": 0,
                        "tier_current": 0,
                        "tier_total": 7
                    }
                ],
                "units": [
                    {
                        "character_id": "TFT15_Kennen",
                        "itemNames": [],
                        "name": "",
                        "rarity": 0,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Malphite",
                        "itemNames": [],
                        "name": "",
                        "rarity": 0,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_Rakan",
                        "itemNames": [],
                        "name": "",
                        "rarity": 1,
                        "tier": 1
                    },
                    {
                        "character_id": "TFT15_Janna",
                        "itemNames": [],
                        "name": "",
                        "rarity": 1,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Neeko",
                        "itemNames": [
                            "TFT_Item_ThiefsGloves",
                            "TFT_Item_EmptyBag",
                            "TFT_Item_EmptyBag"
                        ],
                        "name": "",
                        "rarity": 2,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Akali",
                        "itemNames": [
                            "TFT5_Item_GuinsoosRagebladeRadiant",
                            "TFT_Item_GuardianAngel",
                            "TFT_Item_ArchangelsStaff"
                        ],
                        "name": "",
                        "rarity": 4,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_KSante",
                        "itemNames": [
                            "TFT_Item_Redemption",
                            "TFT_Item_GargoyleStoneplate",
                            "TFT_Item_BrambleVest"
                        ],
                        "name": "",
                        "rarity": 4,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Ryze",
                        "itemNames": [
                            "TFT4_Item_OrnnTheCollector",
                            "TFT_Item_RapidFireCannon",
                            "TFT_Item_StatikkShiv"
                        ],
                        "name": "",
                        "rarity": 4,
                        "tier": 2
                    },
                    {
                        "character_id": "TFT15_Varus",
                        "itemNames": [
                            "TFT_Item_PowerGauntlet",
                            "TFT_Item_InfinityEdge",
                            "TFT_Item_GuinsoosRageblade"
                        ],
                        "name": "",
                        "rarity": 6,
                        "tier": 2
                    }
                ],
                "win": true
            }
        ],
        "queueId": 1090,
        "queue_id": 1090,
        "tft_game_type": "standard",
        "tft_set_core_name": "TFTSet15",
        "tft_set_number": 15
    }
}
```
<br>

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











