package org.project.domain.match;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Match {
    private Long id;
    private String matchId;
    // 경기 상세 정보 필드들은 추후 추가될 예정입니다.
}
