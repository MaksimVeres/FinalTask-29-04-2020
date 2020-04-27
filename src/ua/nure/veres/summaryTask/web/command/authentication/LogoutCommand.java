package ua.nure.veres.summaryTask.web.command.authentication;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.ee.security.SecurityManager;
import ua.nure.veres.summaryTask.exception.command.authentication.LogoutException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Logout command.
 */
public class LogoutCommand extends AuthCommand {

    private static final long serialVersionUID = -2785976616686657267L;

    private static final Logger LOG = Logger.getLogger(LogoutCommand.class);

    static {
        BasicConfigurator.configure();
    }

    public LogoutCommand() {
        super(SecurityManager.getInstance());
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws LogoutException {
        LOG.debug("Command starts");

        HttpSession session = request.getSession(false);
        User user = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_USER, user));

        if (user == null) {
            throw new LogoutException(LogoutException.MESSAGE_SESSION_HAS_NO_SUCH_USER);
        }

        removeLoggedUser(user);
        session.invalidate();
        LOG.debug("Command finished");
        return Path.COMMAND_REDIRECT_TO_LOGIN_PAGE;
    }

}
