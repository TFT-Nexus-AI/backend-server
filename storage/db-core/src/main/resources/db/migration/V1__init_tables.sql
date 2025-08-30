-- 유저 정보를 저장하는 테이블
CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    puuid      VARCHAR(255) NOT NULL UNIQUE,
    game_name  VARCHAR(255) NOT NULL,
    tag_line   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 매치 히스토리 정보를 저장하는 테이블
CREATE TABLE match_history
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT,
    match_id      VARCHAR(255) NOT NULL UNIQUE,
    game_creation BIGINT,
    game_duration BIGINT,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);