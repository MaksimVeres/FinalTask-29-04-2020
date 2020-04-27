package ua.nure.veres.summaryTask.exception.command.authentication;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on a log in authentication error.
 */
public class LogInException extends AppException {

    private static final long serialVersionUID = 910345456128880392L;

    public static final String MESSAGE_SAME_USER_IS_ALREADY_LOGGED_IN = "Same user is already logged in";

    public static final String MESSAGE_USER_WITH_SUCH_LOGIN_OR_PASSWORD_DOES_NOT_EXISTS
            = "Cannot find user with such login/password";

    public LogInException(String message) {
        super(message);
    }
}
