package ua.nure.veres.summaryTask.exception.command.access;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on a access error.
 */
public class CommandAccessException extends AppException {

    private static final long serialVersionUID = 943548745612888392L;

    public static final String MESSAGE_FORBIDDEN = "Forbidden command";

    public static final String MESSAGE_UNAUTHORIZED = "User is not authorized to command";

    public CommandAccessException(String message) {
        super(message);
    }
}
