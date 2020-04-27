package ua.nure.veres.summaryTask.exception;

/**
 * An abstract exception that provides information on an application error.
 */
public abstract class AppException extends Exception {

    public AppException() {
        super();
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(String message) {
        super(message);
    }

}
