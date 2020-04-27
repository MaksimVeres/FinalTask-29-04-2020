package ua.nure.veres.summaryTask.web.command.forward.request;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.command.forward.ForwardCommandException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * To describe form command.
 */
public class ToDescribeFormCommand extends ToRequestFormForwardCommand {

    private static final long serialVersionUID = 7565654342322222434L;

    private static final String ATTRIBUTE_USER_DESCRIBE_USER_SERVICE_ID = "userDescribeUserServiceId";

    private static final Logger LOG = Logger.getLogger(ToDescribeFormCommand.class);

    static {
        BasicConfigurator.configure();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        validateForwardMethod(request);
        String forwardMethod = getForwardMethod(request);
        String userServiceId = getParameter(PARAMETER_USER_SERVICE_ID, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_USER_SERVICE_ID, userServiceId));

        if (!validateParameter(userServiceId)) {
            throw new ParameterException(PARAMETER_USER_SERVICE_ID);
        }

        String tariffId = getParameter(PARAMETER_TARIFF_ID, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_TARIFF_ID, tariffId));

        if (!validateParameter(tariffId)) {
            throw new ParameterException(PARAMETER_TARIFF_ID);
        }

        if (FORWARD_METHOD_REDIRECT.equals(forwardMethod)) {
            throw new ForwardCommandException(ForwardCommandException.MESSAGE_NO_REDIRECT_METHOD_SUPPORTED);
        }

        HttpSession session = request.getSession();
        Object user = getAttribute(ATTRIBUTE_USER, session);
        LOG.trace("Session attribute: user --> " + user);

        if (user == null) {
            LOG.debug("Command finished");
            return Path.MOVE_BY_FORWARD_TO_LOGIN_PAGE;
        }

        try {
            long userServiceIdValue = Long.parseLong(userServiceId);
            prepareRequest(tariffId, request);
            setAttribute(ATTRIBUTE_USER_DESCRIBE_USER_SERVICE_ID, userServiceIdValue, session);
            LOG.info(String.format(
                    "Set session attribute %s --> %d", ATTRIBUTE_USER_DESCRIBE_USER_SERVICE_ID, userServiceIdValue)
            );
            LOG.debug("Command finished");

            return Path.MOVE_BY_FORWARD_TO_DESCRIBE_PAGE;
        } catch (NumberFormatException ex) {
            throw new ParameterException(PARAMETER_USER_SERVICE_ID);
        }
    }
}
