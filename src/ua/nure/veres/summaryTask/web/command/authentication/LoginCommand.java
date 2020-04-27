package ua.nure.veres.summaryTask.web.command.authentication;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.ee.PaymentManager;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.user.UserIdentityException;
import ua.nure.veres.summaryTask.exception.command.authentication.LogInException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.ee.security.SecurityManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Login command.
 */
public class LoginCommand extends AuthCommand {

    private static final long serialVersionUID = -3071536593627692473L;

    private static final Logger LOG = Logger.getLogger(LoginCommand.class);

    private static final String PARAMETER_LOGIN = "login";

    private static final String PARAMETER_PASSWORD = "password";

    private transient DaoFactory userDaoFactory;

    private transient PaymentManager paymentManager;

    public LoginCommand(SecurityManager securityManager,
                        DaoFactory userDaoFactory,
                        PaymentManager paymentManager) {
        super(securityManager);
        this.userDaoFactory = userDaoFactory;
        this.paymentManager = paymentManager;
    }

    static {
        BasicConfigurator.configure();
    }


    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException {
        LOG.debug("Command starts");

        HttpSession session = request.getSession();

        // obtain login and password from a request
        String login = getParameter(PARAMETER_LOGIN, request);
        LOG.trace("Request parameter: login --> " + login);

        String password = getParameter(PARAMETER_PASSWORD, request);
        if (!validateParameter(login) || !validateParameter(password)) {
            throw new UserIdentityException(UserIdentityException.MESSAGE_LOGIN_OR_PASSWORD_IS_EMPTY);
        }

        Dao<User, Long> userDao = userDaoFactory.getDao();

        User user = userDao.customReadOne("READ ONE login", login);
        LOG.trace("Found in DB: user --> " + user);


        if (user == null || !securityManager.checkPassword(password, user)) {
            throw new LogInException(LogInException.MESSAGE_USER_WITH_SUCH_LOGIN_OR_PASSWORD_DOES_NOT_EXISTS);
        }

        if (checkAuthorized(user.getLogin())) {
            throw new LogInException(LogInException.MESSAGE_SAME_USER_IS_ALREADY_LOGGED_IN);
        }

        Role userRole = Role.getRole(user);
        LOG.trace("userRole --> " + userRole);

        String forward = Path.COMMAND_REDIRECT_TO_MAIN_PAGE;

        LOG.trace("Checking economic stability to user --> " + user);
        paymentManager.checkEconomicStability(user);

        setAttribute(ATTRIBUTE_USER, user, session);
        setLoggedUser(user);
        LOG.trace(String.format("Set the session attribute: %s --> %s", ATTRIBUTE_USER, user));
        LOG.info(String.format("User %s logged as %s", user, userRole.toString().toLowerCase(Locale.getDefault())));
        LOG.debug("Command finished");

        return forward;
    }
}
