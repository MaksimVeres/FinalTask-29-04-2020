package ua.nure.veres.summaryTask.exception.ee.security;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on security manager error.
 */
public class SecurityManagerException extends AppException {

    private static final long serialVersionUID = 564758843234265L;

    public static final String MESSAGE_ALGORITHM_EXCEPTION = "Security manager algorithm exception";

    public SecurityManagerException(String message) {
        super(message);
    }
}
