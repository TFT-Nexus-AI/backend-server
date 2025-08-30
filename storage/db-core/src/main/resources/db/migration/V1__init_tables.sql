-- 유저 정보를 저장하는 테이블
CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       puuid VARCHAR(78) UNIQUE NOT NULL,
                       game_name VARCHAR(255) NOT NULL,
                       tag_line VARCHAR(255) NOT NULL,
                       summoner_level BIGINT,
                       profile_icon_id INT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
-- 매치 루트 엔티티
CREATE TABLE matches (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         match_id VARCHAR(255) UNIQUE NOT NULL,
                         game_version VARCHAR(50),
                         game_length FLOAT,
                         game_creation BIGINT,
                         tft_set_number INT,
                         tft_set_core_name VARCHAR(100),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 참여자 정보
CREATE TABLE participants (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              match_id BIGINT REFERENCES matches(id),
                              puuid VARCHAR(78) NOT NULL,
                              placement INT NOT NULL,
                              level INT,
                              last_round INT,
                              gold_left INT,
                              total_damage_to_players INT,
                              players_eliminated INT,
                              time_eliminated FLOAT
);

-- 특성 정보
CREATE TABLE participant_traits (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    participant_id BIGINT REFERENCES participants(id),
                                    name VARCHAR(100) NOT NULL,
                                    num_units INT,
                                    style INT,
                                    tier_current INT,
                                    tier_total INT
);

-- 유닛 정보
CREATE TABLE participant_units (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   participant_id BIGINT REFERENCES participants(id),
                                   character_id VARCHAR(100) NOT NULL,
                                   name VARCHAR(100),
                                   rarity INT,
                                   tier INT
);

-- 아이템 정보
CREATE TABLE participant_items (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   unit_id BIGINT REFERENCES participant_units(id),
                                   item_id INT NOT NULL,
                                   item_name VARCHAR(100)
);
```

### 4.3 Analysis Aggregate

```sql
-- 분석 루트 엔티티
CREATE TABLE analyses (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          user_id BIGINT REFERENCES users(id),
                          analysis_type VARCHAR(50) NOT NULL,
                          status VARCHAR(20) DEFAULT 'PROCESSING',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          completed_at TIMESTAMP NULL
);

-- 분석 결과
CREATE TABLE analysis_results (
                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  analysis_id BIGINT REFERENCES analyses(id),
                                  title VARCHAR(255) NOT NULL,
                                  content TEXT NOT NULL,
                                  insight_type VARCHAR(50),
                                  confidence_score DECIMAL(3,2)
);

-- 분석에 사용된 매치들
CREATE TABLE analysis_matches (
                                  analysis_id BIGINT REFERENCES analyses(id),
                                  match_id BIGINT REFERENCES matches(id),
                                  PRIMARY KEY (analysis_id, match_id)
);