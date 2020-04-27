package ua.nure.veres.summaryTask.web.command.get;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Abstract get command.
 */
public abstract class GetCommand extends Command {

    public static final String COMMAND_EXECUTION_SUCCESS = "Command executed successfully";

    private static final String COMMAND_EXECUTED = "Command executed";

    private static final String ATTRIBUTE_USER = "user";

    private static final Logger LOG = Logger.getLogger(GetCommand.class);

    static {
        BasicConfigurator.configure();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException {
        LOG.debug("Command starts");
        String result = doGetCommand(request, response);
        LOG.debug("Command finished");
        return COMMAND_EXECUTED.equals(result) ? COMMAND_EXECUTION_SUCCESS : result;
    }

    /**
     * Checks user to have admin access.
     *
     * @param session HttpSession
     */
    protected void checkUserAccess(HttpSession session) throws CommandAccessException {
        User user = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace("Session attribute: user --> " + user);

        if (user == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        LOG.info(String.format("User %s logged as %s"
                , user, Role.getRole(user).toString().toLowerCase(Locale.getDefault())));

        if (!checkAccess(user, Role.ADMIN)) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_FORBIDDEN);
        }
    }

    /**
     * Get command logic
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return result of execution
     */
    protected abstract String doGetCommand(HttpServletRequest request, HttpServletResponse response)
            throws AppException;
}
