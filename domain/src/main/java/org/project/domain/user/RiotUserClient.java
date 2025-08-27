package org.project.domain.user;

public interface RiotUserClient {
    User getUserFromRiotApi(String gameName, String tagLine);
}
