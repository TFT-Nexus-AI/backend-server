package org.project.app.user;

public class FakeRiotTokenProvider implements RiotTokenProvider {
    @Override
    public String getAccessToken(String code) {
        return "test-access-token";
    }
}
