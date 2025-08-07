package org.project.app.user;

import org.project.app.auth.RiotTokenProvider;

public class FakeRiotTokenProvider implements RiotTokenProvider {
    @Override
    public String getAccessToken(String code) {
        return "test-access-token";
    }
}
