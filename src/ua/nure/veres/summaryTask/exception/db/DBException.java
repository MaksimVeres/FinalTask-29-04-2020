package ua.nure.veres.summaryTask.exception.db;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on a database access error.
 */
public class DBException extends AppException {

    private static final long serialVersionUID = -3550446897536410392L;

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }

}