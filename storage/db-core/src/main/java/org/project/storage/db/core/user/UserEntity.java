package org.project.storage.db.core.user;

import jakarta.persistence.*;
import lombok.*;
import org.project.domain.user.User;
import org.project.storage.db.core.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "puuid")
public class UserEntity extends BaseEntity {


    @Column(name = "puuid", unique = true, nullable = false, length = 100)
    private String puuid;

    @Column(name = "game_name", nullable = false, length = 50)
    private String gameName;

    @Column(name = "tag_line", nullable = false, length = 10)
    private String tagLine;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private UserEntity(String puuid, String gameName, String tagLine, LocalDateTime createdAt) {
        this.puuid = puuid;
        this.gameName = gameName;
        this.tagLine = tagLine;
        this.createdAt = createdAt;
    }

    public User toDomain() {
        return User.builder()
                .puuid(this.puuid)
                .gameName(this.gameName)
                .tagLine(this.tagLine)
                .createdAt(this.createdAt)
                .build();
    }

    public static UserEntity fromDomain(User user) {
        return UserEntity.builder()
                .puuid(user.getPuuid())
                .gameName(user.getGameName())
                .tagLine(user.getTagLine())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
