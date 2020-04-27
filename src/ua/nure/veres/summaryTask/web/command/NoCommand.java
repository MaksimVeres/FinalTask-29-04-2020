package ua.nure.veres.summaryTask.web.command;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * No command.
 */
public class NoCommand extends Command {

    private static final long serialVersionUID = -2785976616686657267L;

    private static final Logger LOG = Logger.getLogger(NoCommand.class);

    private static final String MESSAGE_ERROR = "No such command";

    static {
        BasicConfigurator.configure();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        LOG.debug("Command starts");

        request.setAttribute("errorMessage", MESSAGE_ERROR);
        LOG.error("Set the request attribute: errorMessage --> " + MESSAGE_ERROR);

        LOG.debug("Command finished");
        return Path.PAGE_ERROR;
    }

}