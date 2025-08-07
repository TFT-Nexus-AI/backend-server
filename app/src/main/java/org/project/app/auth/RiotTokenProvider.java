package org.project.app.auth;


public interface RiotTokenProvider {
    String getAccessToken(String code);
}
