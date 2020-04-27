package ua.nure.veres.summaryTask.web.command.change.user;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.ee.security.SecurityManager;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.db.DBException;
import ua.nure.veres.summaryTask.exception.ee.security.SecurityManagerException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.change.ChangeCommand;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

/**
 * Password change command.
 */
public class ChangePasswordCommand extends ChangeCommand {

    private static final long serialVersionUID = 12222345465600L;

    private static final String PARAMETER_NEW_PASSWORD = "newPassword";

    private static final String PARAMETER_OLD_PASSWORD = "oldPassword";

    private static final String ATTRIBUTE_LOGGED_USER = "LOGGED_USER: ";

    private static final Logger LOG = Logger.getLogger(ChangePasswordCommand.class);

    private transient DaoFactory userDaoFactory;

    private transient SecurityManager securityManager;

    static {
        BasicConfigurator.configure();
    }

    public ChangePasswordCommand(DaoFactory userDaoFactory, SecurityManager securityManager) {
        this.userDaoFactory = userDaoFactory;
        this.securityManager = securityManager;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        String newPassword = getParameter(PARAMETER_NEW_PASSWORD, request);
        if (!validateParameter(newPassword)) {
            throw new ParameterException(PARAMETER_NEW_PASSWORD);
        }

        String oldPassword = getParameter(PARAMETER_OLD_PASSWORD, request);
        if (!validateParameter(oldPassword)) {
            throw new ParameterException(PARAMETER_OLD_PASSWORD);
        }

        HttpSession session = request.getSession();
        ServletContext context = request.getServletContext();

        User user = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace("Session attribute: " + ATTRIBUTE_USER + " --> " + user);

        if (user == null || getAttribute(ATTRIBUTE_LOGGED_USER + user.getLogin(), context) == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        if (!securityManager.checkPassword(oldPassword, user)) {
            throw new UserUpdateException(UserUpdateException.MESSAGE_OLD_PASSWORD_DO_NOT_MUCH);
        }

        try {
            String hashedPassword = securityManager.hashString(newPassword);
            LOG.trace("Password was hashed");

            Dao<User, Long> userDao = userDaoFactory.getDao();

            User dbUser = userDao.customReadOne("READ ONE login", user.getLogin());
            LOG.trace("Found in DB: user --> " + dbUser);

            if (dbUser == null) {
                throw new UserUpdateException(UserUpdateException.MESSAGE_NO_SUCH_USER_IN_DB);
            }

            dbUser.setPassword(hashedPassword);
            userDao.update(dbUser);
            LOG.info("Password of user with login [" + dbUser.getLogin() + "] was changed");
            LOG.debug("Command finished");

            return Path.MOVE_BY_REDIRECT_TO_PERSONAL_CABINET_PAGE;
        } catch (DBException ex) {
            throw new UserUpdateException(UserUpdateException.MESSAGE_CANNOT_DO_USER_UPDATE);
        } catch (NoSuchAlgorithmException ex) {
            throw new SecurityManagerException(SecurityManagerException.MESSAGE_ALGORITHM_EXCEPTION);
        }
    }

}
