package ua.nure.veres.summaryTask.exception.tariff;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on tariff create error.
 */
public class TariffCreateException extends AppException {

    private static final long serialVersionUID = 999123656758456L;

    public static final String MESSAGE_SUCH_TARIFF_ALREADY_IN_DB
            = "Cannot create the tariff: such tariff name is already used in a database";

    public static final String MESSAGE_NOT_NUMBER_PARAMETER_WAS_GIVEN
            = "Cannot create the tariff: not number parameter was given";

    public static final String MESSAGE_NO_SUCH_SERVICE_IN_DB
            = "Cannot create the tariff: no such service in database to link for the tariff";

    public TariffCreateException(String message) {
        super(message);
    }

}
