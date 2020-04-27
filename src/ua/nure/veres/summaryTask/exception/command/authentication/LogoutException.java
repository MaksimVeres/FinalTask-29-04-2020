package ua.nure.veres.summaryTask.exception.command.authentication;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on an logout error.
 */
public class LogoutException extends AppException {

    private static final long serialVersionUID = 723345476528880392L;

    public static final String MESSAGE_SESSION_HAS_NO_SUCH_USER = "Session has no such user";

    public LogoutException(String message) {
        super(message);
    }
}
