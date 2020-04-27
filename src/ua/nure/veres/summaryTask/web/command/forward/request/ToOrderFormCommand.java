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
 * To order form command.
 */
public class ToOrderFormCommand extends ToRequestFormForwardCommand {
    private static final long serialVersionUID = 66784534529756656L;

    private static final Logger LOG = Logger.getLogger(ToOrderFormCommand.class);

    static {
        BasicConfigurator.configure();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        validateForwardMethod(request);
        String forwardMethod = getForwardMethod(request);
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
            return Path.MOVE_BY_FORWARD_TO_LOGIN_PAGE;
        }

        try {
            prepareRequest(tariffId, request);
            LOG.debug("Command finished");
            return Path.MOVE_BY_FORWARD_TO_ORDER_PAGE;
        } catch (NumberFormatException ex) {
            throw new ParameterException(PARAMETER_TARIFF_ID);
        }
    }
}
