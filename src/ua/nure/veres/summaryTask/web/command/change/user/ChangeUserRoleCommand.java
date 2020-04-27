package ua.nure.veres.summaryTask.web.command.change.user;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.change.ChangeCommand;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

/**
 * Change user role command.
 */
public class ChangeUserRoleCommand extends ChangeCommand {

    private static final long serialVersionUID = 4441235466666666677L;

    private static final String PARAMETER_LOGIN = "login";

    private static final String PARAMETER_ROLE_ID = "roleId";

    private static final String ATTRIBUTE_REPLY_REQUEST = "replyRequest";

    private static final Logger LOG = Logger.getLogger(ChangeUserRoleCommand.class);

    private transient DaoFactory userDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public ChangeUserRoleCommand(DaoFactory userDaoFactory) {
        this.userDaoFactory = userDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws IOException
            , ServletException, AppException {

        LOG.debug("Command starts");

        String login = getParameter(PARAMETER_LOGIN, request);
        String roleId = getParameter(PARAMETER_ROLE_ID, request);
        LOG.trace(String.format("Requested parameter: %s --> %s", PARAMETER_LOGIN, login));
        LOG.trace(String.format("Requested parameter: %s --> %s", PARAMETER_ROLE_ID, roleId));

        if (!validateParameter(login)) {
            throw new ParameterException(PARAMETER_LOGIN);
        }

        if (!validateParameter(roleId)) {
            throw new ParameterException(PARAMETER_ROLE_ID);
        }

        int roleIdValue;
        try {
            roleIdValue = Integer.parseInt(roleId);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParameterException(PARAMETER_ROLE_ID);
        }

        HttpSession session = request.getSession();
        User sessionUser = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_USER, sessionUser));

        if (sessionUser == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        LOG.info(String.format("User %s logged as %s"
                , sessionUser, Role.getRole(sessionUser).toString().toLowerCase(Locale.getDefault())));

        if (!checkAccess(sessionUser, Role.ADMIN)) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_FORBIDDEN);
        }

        Dao<User, Long> userDao = userDaoFactory.getDao();
        User user = userDao.customReadOne("READ ONE login", login);
        LOG.trace("Found in DB: user --> " + user);

        if (user == null) {
            throw new UserUpdateException(UserUpdateException.MESSAGE_NO_SUCH_USER_IN_DB);
        }

        user.setRoleId(roleIdValue);
        userDao.update(user);
        LOG.info(String.format("Role of user with login [%s] was changed to %s"
                , user.getLogin(), Role.getRole(user))
        );

        String replyRequest = (String) getAttribute(ATTRIBUTE_REPLY_REQUEST, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_REPLY_REQUEST, replyRequest));

        LOG.debug("Command finished");

        if (replyRequest == null) {
            return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_USER_LIST;
        }

        return replyRequest;
    }
}
