package ua.nure.veres.summaryTask.web.command.user;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Replenish user account command.
 */
public class ReplenishUserAccountCommand extends Command {

    private static final long serialVersionUID = -90080070062315546L;

    private static final String PARAMETER_LOGIN = "login";

    private static final String PARAMETER_VALUE = "value";

    private static final Logger LOG = Logger.getLogger(ReplenishUserAccountCommand.class);

    private transient DaoFactory userDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public ReplenishUserAccountCommand(DaoFactory userDaoFactory) {
        this.userDaoFactory = userDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
            , AppException {
        LOG.debug("Command starts");

        String loginParameter = getParameter(PARAMETER_LOGIN, request);
        String valueParameter = getParameter(PARAMETER_VALUE, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_LOGIN, loginParameter));
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_VALUE, valueParameter));

        if (!validateParameter(loginParameter)) {
            throw new ParameterException(PARAMETER_LOGIN);
        }

        if (!validateParameter(valueParameter)) {
            throw new ParameterException(PARAMETER_VALUE);
        }

        double value;
        try {
            value = Double.parseDouble(valueParameter);
            if (value < 0) {
                throw new ParameterException(PARAMETER_VALUE);
            }
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParameterException(PARAMETER_VALUE);
        }

        Dao<User, Long> userDao = userDaoFactory.getDao();
        User user = userDao.customReadOne("READ ONE login", loginParameter);
        LOG.trace("Found in DB: user --> " + user);

        if (user == null) {
            throw new UserUpdateException(UserUpdateException.MESSAGE_NO_SUCH_USER_IN_DB);
        }

        user.setAccountState(user.getAccountState() + value);
        userDao.update(user);
        LOG.info(String.format(
                "Account of user with login [%s] was replenished with an amount of --> %s, new account state --> %s"
                , user.getLogin(), value, user.getAccountState()
        ));

        LOG.debug("Command finished");
        return Path.MOVE_BY_REDIRECT_TO_ACCOUNT_REPLENISHED_PAGE;
    }

}
