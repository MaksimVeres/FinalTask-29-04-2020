package ua.nure.veres.summaryTask.exception.command.value;

import ua.nure.veres.summaryTask.exception.AppException;

/**
 * An exception that provides information on attribute error.
 */
public class AttributeException extends AppException {

    private static final long serialVersionUID = 158778992888392L;

    private static final String ATTRIBUTE_EXPECTED = " attribute expected";

    public AttributeException(String attributeName) {
        super(attributeName + " " + ATTRIBUTE_EXPECTED);
    }
}
