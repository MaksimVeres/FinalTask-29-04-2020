package ua.nure.veres.summaryTask.exception.command.registration;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on a registration error.
 */
public class RegisterException extends AppException {

    private static final long serialVersionUID = -910345456128880392L;

    public static final String MESSAGE_SUCH_USER_IS_ALREADY_REGISTERED = "Such user is already registered";

    public static final String MESSAGE_LOGIN_OR_PASSWORD_IS_EMPTY = "Login/password cannot be empty";

    public RegisterException(String message) {
        super(message);
    }
}
