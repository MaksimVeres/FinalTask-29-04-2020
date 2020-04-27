package ua.nure.veres.summaryTask.exception.tariff;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on tariff update error.
 */
public class TariffUpdateException extends AppException {

    private static final long serialVersionUID = 999123656758456L;

    public static final String MESSAGE_SUCH_NAME_ALREADY_IN_DB
            = "Cannot do tariff update: such tariff name is already used in database";

    public static final String MESSAGE_NOT_NUMBER_PARAMETER_WAS_GIVEN
            = "Cannot do tariff update: not number parameter was given";

    public static final String MESSAGE_NO_SUCH_TARIFF_IN_DB = "Cannot do tariff update: no such tariff in database";

    public static final String MESSAGE_NO_SUCH_SERVICE_IN_DB
            = "Cannot create the tariff: no such service in database to link for the tariff";

    public TariffUpdateException(String message) {
        super(message);
    }
}
