package ua.nure.veres.summaryTask.web.command.delete;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userDescribe.UserDescribeDaoFactory;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserDescribe;
import ua.nure.veres.summaryTask.db.entity.UserOrder;
import ua.nure.veres.summaryTask.db.entity.UserService;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Delete user command.
 */
public class DeleteUserCommand extends Command {

    private static final long serialVersionUID = 70120214000000L;

    private static final String PARAMETER_ID = "id";

    private static final String ATTRIBUTE_USER = "user";

    private static final String ATTRIBUTE_REPLY_REQUEST = "replyRequest";

    private static final Logger LOG = Logger.getLogger(DeleteUserCommand.class);

    private transient UserDaoFactory userDaoFactory;

    private transient UserServiceDaoFactory userServiceDaoFactory;

    private transient UserOrderDaoFactory userOrderDaoFactory;

    private transient UserDescribeDaoFactory userDescribeDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public DeleteUserCommand(
            UserDaoFactory userDaoFactory,
            UserServiceDaoFactory userServiceDaoFactory,
            UserOrderDaoFactory userOrderDaoFactory,
            UserDescribeDaoFactory userDescribeDaoFactory
    ) {
        this.userDaoFactory = userDaoFactory;
        this.userServiceDaoFactory = userServiceDaoFactory;
        this.userOrderDaoFactory = userOrderDaoFactory;
        this.userDescribeDaoFactory = userDescribeDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws IOException
            , ServletException, AppException {

        LOG.debug("Command starts");

        String parameterId = getParameter(PARAMETER_ID, request);
        LOG.trace(String.format("Requested parameter: %s --> %s", PARAMETER_ID, parameterId));

        if (!validateParameter(parameterId)) {
            throw new ParameterException(PARAMETER_ID);
        }

        HttpSession session = request.getSession();

        User sessionUser = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_USER, sessionUser));

        if (sessionUser == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        LOG.info(String.format("User %s logged as %s"
                , sessionUser, Role.getRole(sessionUser).toString().toLowerCase(Locale.getDefault()))
        );

        if (!checkAccess(sessionUser, Role.ADMIN)) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_FORBIDDEN);
        }

        long id;
        try {
            id = Long.parseLong(parameterId);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParameterException(PARAMETER_ID);
        }

        Dao<User, Long> userDao = userDaoFactory.getDao();
        User user = userDao.read(id);
        LOG.trace("Found in DB: user --> " + user);

        if (user == null) {
            LOG.debug("Command finished");
            return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_USER_LIST;
        }

        Dao<UserService, Long> userServiceDao = userServiceDaoFactory.getDao();
        List<UserService> userServices = userServiceDao.customReadMany("READ MANY userId", id);
        LOG.trace("Found in DB: linked user services, size --> " + userServices.size());

        for (UserService userService : userServices) {
            userServiceDao.delete(userService);
        }
        LOG.info(String.format("%s linked user services to user [%s] was deleted"
                , userServices.size(), user.getLogin()));

        Dao<UserOrder, Long> userOrderDao = userOrderDaoFactory.getDao();
        List<UserOrder> userOrders = userOrderDao.customReadMany("READ MANY userId", id);
        LOG.trace("Found in DB: linked user orders, size --> " + userOrders.size());

        for (UserOrder userOrder : userOrders) {
            userOrderDao.delete(userOrder);
        }
        LOG.info(String.format("%s linked user orders to user [%s] was deleted"
                , userOrders.size(), user.getLogin()));

        Dao<UserDescribe, Long> userDescribeDao = userDescribeDaoFactory.getDao();
        List<UserDescribe> userDescribes = userDescribeDao.customReadMany(
                "READ MANY User.login", user.getLogin()
        );
        LOG.trace("Found in DB: linked user describes, size --> " + userDescribes.size());

        for (UserDescribe userDescribe : userDescribes) {
            userDescribeDao.delete(userDescribe);
        }
        LOG.info(String.format("%s linked user describes to user [%s] was deleted"
                , userDescribes.size(), user.getLogin()));

        userDao.delete(user);
        LOG.info("User was deleted --> " + user);

        LOG.debug("Command finished");

        String replyRequest = (String) getAttribute(ATTRIBUTE_REPLY_REQUEST, session);
        LOG.trace("Session attribute: replyRequest --> " + replyRequest);

        if (replyRequest != null) {
            return replyRequest;
        }

        return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_USER_LIST;
    }
}
