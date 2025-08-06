package org.project.app.user;

import lombok.Getter;
import org.project.domain.user.User;

@Getter
public class LoginResult {
    private final boolean success;
    private final User user;
    private final String errorMessage;

    public LoginResult(boolean success, User user, String errorMessage) {
        this.success = success;
        this.user = user;
        this.errorMessage = errorMessage;
    }

    public static LoginResult success(User user) {
        return new LoginResult(true, user, null);
    }

    public static LoginResult failure(String errorMessage) {
        return new LoginResult(false, null, errorMessage);
    }
}
