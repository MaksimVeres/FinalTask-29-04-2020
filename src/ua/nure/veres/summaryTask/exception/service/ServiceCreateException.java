package ua.nure.veres.summaryTask.exception.service;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on service create error.
 */
public class ServiceCreateException extends AppException {

    private static final long serialVersionUID = 999123656758456L;

    public static final String MESSAGE_SUCH_SERVICE_ALREADY_IN_DB
            = "Cannot create service: such service name is already used in database";

    public ServiceCreateException(String message) {
        super(message);
    }

}
