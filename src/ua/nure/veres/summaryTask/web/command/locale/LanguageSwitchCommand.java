package ua.nure.veres.summaryTask.web.command.locale;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Language switch command.
 * Switches an user language attribute to a chosen one.
 */
public class LanguageSwitchCommand extends Command {

    private static final long serialVersionUID = 1010L;

    private static final String PARAMETER_LANG = "lang";

    private static final String ATTRIBUTE_LANG = "lang";

    private static final Logger LOG = Logger.getLogger(LanguageSwitchCommand.class);

    static {
        BasicConfigurator.configure();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        String lang = getParameter(PARAMETER_LANG, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_LANG, lang));

        if (!validateParameter(lang)) {
            throw new ParameterException(PARAMETER_LANG);
        }

        HttpSession session = request.getSession();
        session.setAttribute(ATTRIBUTE_LANG, lang);
        LOG.trace(String.format("Set the session attribute: %s --> %s"
                , ATTRIBUTE_LANG, lang));

        LOG.debug("Command finished");
        return Path.MOVE_BY_REDIRECT_TO_MAIN_PAGE;
    }
}
