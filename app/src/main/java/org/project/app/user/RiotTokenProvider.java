package org.project.app.user;


public interface RiotTokenProvider {
    String getAccessToken(String code);
}
