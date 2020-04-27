package ua.nure.veres.summaryTask.web.command.change.user;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.change.ChangeCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Change full name command.
 */
public class ChangeFullNameCommand extends ChangeCommand {

    private static final long serialVersionUID = 65453456888832L;

    private static final String PARAMETER_FIRST_NAME = "firstName";

    private static final String PARAMETER_LAST_NAME = "lastName";

    private static final Logger LOG = Logger.getLogger(ChangeFullNameCommand.class);

    private transient DaoFactory userDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public ChangeFullNameCommand(DaoFactory userDaoFactory) {
        this.userDaoFactory = userDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException {
        LOG.debug("Command starts");

        String firstName = getParameter(PARAMETER_FIRST_NAME, request);
        String lastName = getParameter(PARAMETER_LAST_NAME, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_FIRST_NAME, firstName));
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_LAST_NAME, lastName));

        if (!validateParameter(firstName)) {
            throw new ParameterException(PARAMETER_FIRST_NAME);
        }

        if (!validateParameter(lastName)) {
            throw new ParameterException(PARAMETER_LAST_NAME);
        }

        HttpSession session = request.getSession();
        User user = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_USER, user));

        if (user == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        Dao<User, Long> userDao = userDaoFactory.getDao();

        User dbUser = userDao.customReadOne("READ ONE login", user.getLogin());
        LOG.trace("Found in DB: user --> " + dbUser);

        if (dbUser == null) {
            throw new UserUpdateException(UserUpdateException.MESSAGE_NO_SUCH_USER_IN_DB);
        }

        dbUser.setFirstName(firstName);
        dbUser.setLastName(lastName);
        userDao.update(dbUser);
        LOG.info(String.format("Full name of user with login [%s] was changed to %s %s"
                , dbUser.getLogin(), firstName, lastName)
        );

        LOG.debug("Command finished");
        return Path.MOVE_BY_REDIRECT_TO_PERSONAL_CABINET_PAGE;
    }
}
