package ua.nure.veres.summaryTask.exception.command.value;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on parameter error.
 */
public class ParameterException extends AppException {

    private static final long serialVersionUID = -5463548745612888392L;

    private static final String PARAMETER_EXPECTED = " parameter expected";

    public ParameterException(String parameterName) {
        super(parameterName + " " + PARAMETER_EXPECTED);
    }
}
