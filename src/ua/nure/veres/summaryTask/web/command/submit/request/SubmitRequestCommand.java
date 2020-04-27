package ua.nure.veres.summaryTask.web.command.submit.request;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.command.submit.SubmitCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Submit request abstract command.
 */
abstract class SubmitRequestCommand extends SubmitCommand {

    private static final Logger LOG = Logger.getLogger(SubmitRequestCommand.class);

    static {
        BasicConfigurator.configure();
    }

    /**
     * Gets userRequestId parameter and validates it and user access.
     *
     * @param request request of user
     */
    String getUserRequestId(HttpServletRequest request) throws AppException {
        HttpSession session = request.getSession();
        checkUserAccess(session);
        String userRequestId = getParameter(PARAMETER_USER_REQUEST_ID, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_USER_REQUEST_ID, userRequestId));

        if (!validateParameter(userRequestId)) {
            throw new ParameterException(userRequestId);
        }

        return userRequestId;
    }

    /**
     * Gets user from session and checks it access to Role.ADMIN.
     *
     * @param session session whose user should be checked
     */
    private void checkUserAccess(HttpSession session) throws CommandAccessException {
        User user = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_USER, user));

        if (user == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        LOG.info(String.format("User %s logged as %s", user, Role.getRole(user).toString().toLowerCase(Locale.getDefault())));

        if (!checkAccess(user, Role.ADMIN)) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_FORBIDDEN);
        }
    }

}
