package org.project.app.user;

import org.springframework.stereotype.Component;

@Component
public class RiotTokenProviderImpl implements RiotTokenProvider {

    @Override
    public String getAccessToken(String code) {
        // TODO: 라이엇 API를 호출하여 액세스 토큰을 받아오는 로직을 구현해야 합니다.
        if ("test-authorization-code".equals(code)) {
            return "test-access-token";
        }
        return null;
    }
}
