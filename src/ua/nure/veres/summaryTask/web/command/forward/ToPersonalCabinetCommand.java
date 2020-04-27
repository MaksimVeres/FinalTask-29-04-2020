package ua.nure.veres.summaryTask.web.command.forward;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * To personal cabinet command.
 */
public class ToPersonalCabinetCommand extends ForwardCommand {

    private static final long serialVersionUID = -333376854627692473L;

    private static final Logger LOG = Logger.getLogger(ToPersonalCabinetCommand.class);

    static {
        BasicConfigurator.configure();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        validateForwardMethod(request);
        String forwardMethod = getForwardMethod(request);

        LOG.debug("Command finished");

        if (FORWARD_METHOD_REDIRECT.equals(forwardMethod)) {
            return Path.MOVE_BY_REDIRECT_TO_PERSONAL_CABINET_PAGE;
        }
        return Path.MOVE_BY_FORWARD_TO_PERSONAL_CABINET_PAGE;

    }
}
