package ua.nure.veres.summaryTask.exception.command.forward;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on a log in forward command error.
 */
public class ForwardCommandException extends AppException {

    private static final long serialVersionUID = 100345666691320L;

    public static final String MESSAGE_NO_SUCH_FORWARD_METHOD = "No such forward method";

    public static final String MESSAGE_NO_REDIRECT_METHOD_SUPPORTED = "No redirect method supported";

    public ForwardCommandException(String message) {
        super(message);
    }
}
