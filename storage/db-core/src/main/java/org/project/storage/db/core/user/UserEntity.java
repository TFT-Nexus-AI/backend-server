package org.project.storage.db.core.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "puuid")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "puuid", unique = true, nullable = false, length = 100)
    private String puuid;

    @Column(name = "game_name", nullable = false, length = 50)
    private String gameName;

    @Column(name = "tag_line", nullable = false, length = 10)
    private String tagLine;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public UserEntity(String puuid, String gameName, String tagLine, LocalDateTime createdAt) {
        this.puuid = puuid;
        this.gameName = gameName;
        this.tagLine = tagLine;
        this.createdAt = createdAt;
    }
}
