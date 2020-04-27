package ua.nure.veres.summaryTask.web.command.submit;

import ua.nure.veres.summaryTask.web.command.Command;

/**
 * Abstract submit command.
 */
public abstract class SubmitCommand extends Command {

    protected static final String ATTRIBUTE_USER = "user";

    protected static final String PARAMETER_USER_REQUEST_ID = "userRequestId";

}
