package org.project.app.user;

public interface RiotApiClient {
    RiotUserInfo getUserInfo(String accessToken);
}
