package ua.nure.veres.summaryTask.exception.user;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on a login-password error.
 */
public class UserIdentityException extends AppException {

    private static final long serialVersionUID = 910345456128880392L;

    public static final String MESSAGE_LOGIN_OR_PASSWORD_IS_EMPTY = "Login/password cannot be empty";

    public UserIdentityException(String message) {
        super(message);
    }
}
