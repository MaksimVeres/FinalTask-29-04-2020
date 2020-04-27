package ua.nure.veres.summaryTask.web.command.forward;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.forward.ForwardCommandException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract forward command.
 */
public abstract class ForwardCommand extends Command {
    public static final String FORWARD_METHOD_FORWARD = "forward";

    public static final String FORWARD_METHOD_REDIRECT = "redirect";

    protected static final String ATTRIBUTE_USER = "user";

    private static final String PARAMETER_FORWARD_METHOD = "forwardMethod";

    private static final Logger LOG = Logger.getLogger(ForwardCommand.class);

    static {
        BasicConfigurator.configure();
    }

    /**
     * Validates forward method.
     *
     * @param request servlet request
     */
    protected void validateForwardMethod(HttpServletRequest request) throws AppException {
        String forwardMethod = getParameter(PARAMETER_FORWARD_METHOD, request);
        LOG.trace("Request parameter: " + PARAMETER_FORWARD_METHOD + " --> " + forwardMethod);

        if (forwardMethod == null) {
            throw new ParameterException(PARAMETER_FORWARD_METHOD);
        }
        if (!FORWARD_METHOD_FORWARD.equals(forwardMethod) && !FORWARD_METHOD_REDIRECT.equals(forwardMethod)) {
            throw new ForwardCommandException(ForwardCommandException.MESSAGE_NO_SUCH_FORWARD_METHOD);
        }
    }

    /**
     * Gets forward method
     *
     * @param request to get parameter
     */
    public String getForwardMethod(HttpServletRequest request) {
        String parameter = getParameter(PARAMETER_FORWARD_METHOD, request);
        LOG.trace("Request parameter: " + PARAMETER_FORWARD_METHOD + " --> " + parameter);

        return getParameter(PARAMETER_FORWARD_METHOD, request);
    }

    @Override
    public abstract String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException;
}
