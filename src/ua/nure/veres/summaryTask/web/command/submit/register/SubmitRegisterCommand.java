package ua.nure.veres.summaryTask.web.command.submit.register;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.ee.security.SecurityManager;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.registration.RegisterException;
import ua.nure.veres.summaryTask.exception.ee.security.SecurityManagerException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.submit.SubmitCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;

/**
 * Submit registration command.
 */
public class SubmitRegisterCommand extends SubmitCommand {

    private static final long serialVersionUID = 6765655556566332447L;

    private static final String PARAMETER_LOGIN = "login";

    private static final String PARAMETER_PASSWORD = "password";

    private static final String UNKNOWN_FIRST_NAME = "Unknown first name";

    private static final String UNKNOWN_LAST_NAME = "Unknown last name";

    private static final Logger LOG = Logger.getLogger(SubmitRegisterCommand.class);

    private transient DaoFactory userDaoFactory;

    private transient SecurityManager securityManager;

    static {
        BasicConfigurator.configure();
    }

    public SubmitRegisterCommand(DaoFactory userDaoFactory
            , SecurityManager securityManager) {
        this.userDaoFactory = userDaoFactory;
        this.securityManager = securityManager;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        String login = getParameter(PARAMETER_LOGIN, request);
        String password = getParameter(PARAMETER_PASSWORD, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_LOGIN, login));

        if (!validateParameter(login) || !validateParameter(password)) {
            throw new RegisterException(RegisterException.MESSAGE_LOGIN_OR_PASSWORD_IS_EMPTY);
        }

        Dao<User, Long> userDao = userDaoFactory.getDao();
        User dbUser = userDao.customReadOne("READ ONE login", login);
        LOG.trace("Found in DB (expected null): user --> " + dbUser);

        if (dbUser != null) {
            throw new RegisterException(RegisterException.MESSAGE_SUCH_USER_IS_ALREADY_REGISTERED);
        }

        String hashedPassword;
        try {
            hashedPassword = securityManager.hashString(password);
            LOG.trace("Password was hashed");
        } catch (NoSuchAlgorithmException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SecurityManagerException(SecurityManagerException.MESSAGE_ALGORITHM_EXCEPTION);
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(hashedPassword);
        user.setFirstName(UNKNOWN_FIRST_NAME);
        user.setLastName(UNKNOWN_LAST_NAME);
        user.setRoleId(Role.CUSTOMER.ordinal());
        userDao.create(user);
        LOG.info("User created --> " + user);

        LOG.debug("Command finished");
        return Path.COMMAND_LOGIN;
    }
}