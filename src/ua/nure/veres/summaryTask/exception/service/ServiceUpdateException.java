package ua.nure.veres.summaryTask.exception.service;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on service update error.
 */
public class ServiceUpdateException extends AppException {

    private static final long serialVersionUID = 999123656758456L;

    public static final String MESSAGE_SUCH_NAME_ALREADY_IN_DB
            = "Cannot do service update: such service name is already used in a database";

    public static final String MESSAGE_NO_SUCH_SERVICE_IN_DB
            = "Cannot do service update: no such service in a database";

    public ServiceUpdateException(String message) {
        super(message);
    }
}
