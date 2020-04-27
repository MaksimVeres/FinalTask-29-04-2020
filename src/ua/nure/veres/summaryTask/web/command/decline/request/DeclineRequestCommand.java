package ua.nure.veres.summaryTask.web.command.decline.request;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.command.decline.DeclineCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Abstract decline request command.
 */
abstract class DeclineRequestCommand extends DeclineCommand {

    static final String PARAMETER_USER_REQUEST_ID = "userRequestId";

    private static final String ATTRIBUTE_USER = "user";

    private static final Logger LOG = Logger.getLogger(DeclineRequestCommand.class);

    static {
        BasicConfigurator.configure();
    }

    /**
     * Prepares request to execution.
     *
     * @param request HttpServletRequest
     * @return userRequestId parameter
     */
    String prepareToRequest(HttpServletRequest request) throws AppException {
        HttpSession session = request.getSession();
        User user = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace("Session attribute: user --> " + user);

        if (user == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        LOG.info(String.format("User %s logged as %s"
                , user, Role.getRole(user).toString().toLowerCase(Locale.getDefault()))
        );

        if (!checkAccess(user, Role.ADMIN)) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_FORBIDDEN);
        }

        String userRequestId = getParameter(PARAMETER_USER_REQUEST_ID, request);
        LOG.trace("Request parameter: " + PARAMETER_USER_REQUEST_ID + " --> " + userRequestId);

        if (!validateParameter(userRequestId)) {
            throw new ParameterException(PARAMETER_USER_REQUEST_ID);
        }

        return userRequestId;
    }
}
