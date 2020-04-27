package ua.nure.veres.summaryTask.web.command.order;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.db.Status;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserOrder;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.AttributeException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.util.Locale;

/**
 * Order service command.
 */
public class OrderServiceCommand extends Command {

    private static final long serialVersionUID = -765788986745465899L;

    private static final String PARAMETER_COMMENT = "comment";

    private static final String PARAMETER_PHONE = "phone";

    private static final String ATTRIBUTE_USER = "user";

    private static final String ATTRIBUTE_USER_REQUEST_TARIFF = "userRequestTariff";

    private static final String ATTRIBUTE_USER_REQUEST_SERVICE = "userRequestService";

    private static final Logger LOG = Logger.getLogger(OrderServiceCommand.class);

    private static final String LOGGER_REMOVE_THE_ATTRIBUTE = "Remove the session attribute ";

    private transient UserOrderDaoFactory userOrderDaoFactory;

    private transient UserDaoFactory userDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public OrderServiceCommand(
            UserOrderDaoFactory userOrderDaoFactory,
            UserDaoFactory userDaoFactory
    ) {
        this.userOrderDaoFactory = userOrderDaoFactory;
        this.userDaoFactory = userDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        HttpSession session = request.getSession();

        User user = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_USER, user));

        if (user == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        LOG.info(String.format("User %s has status %s", user
                , Status.getStatus(user).toString().toLowerCase(Locale.getDefault())));

        Tariff userOrderTariff = (Tariff) getAttribute(ATTRIBUTE_USER_REQUEST_TARIFF, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_USER_REQUEST_TARIFF, userOrderTariff));

        if (userOrderTariff == null) {
            throw new AttributeException(ATTRIBUTE_USER_REQUEST_TARIFF);
        }

        if (user.getAccountState() < userOrderTariff.getConnectionPayment()) {
            throw new UserUpdateException(UserUpdateException.MESSAGE_NOT_ENOUGH_MONEY_ON_ACCOUNT);
        }

        if (!checkStatus(user, Status.NORMAL)) {
            throw new UserUpdateException(UserUpdateException.MESSAGE_USER_IS_BLOCKED);
        }

        Dao<User, Long> userDao = userDaoFactory.getDao();
        User dbUser = userDao.customReadOne("READ ONE login", user.getLogin());
        LOG.trace("Found in DB: user --> " + dbUser);

        if (dbUser == null) {
            throw new UserUpdateException(UserUpdateException.MESSAGE_NO_SUCH_USER_IN_DB);
        }

        Dao<UserOrder, Long> userOrderDao = userOrderDaoFactory.getDao();
        UserOrder userOrder = new UserOrder();
        userOrder.setUserId(user.getId());
        userOrder.setTariffId(userOrderTariff.getId());
        userOrder.setComment(getParameter(PARAMETER_COMMENT, request));
        userOrder.setUserPhone(getParameter(PARAMETER_PHONE, request));
        userOrder.setOrderDate(new Date(System.currentTimeMillis()));
        userOrderDao.create(userOrder);
        LOG.info("User order created --> " + userOrder);

        dbUser.setAccountState(dbUser.getAccountState() - userOrderTariff.getConnectionPayment());
        userDao.update(dbUser);
        LOG.info(String.format(
                "Account of user with login [%s] was decreased with an amount of --> %s, new account state --> %s"
                , dbUser.getLogin(), userOrderTariff.getConnectionPayment(), dbUser.getAccountState()
        ));

        removeAttribute(ATTRIBUTE_USER_REQUEST_TARIFF, session);
        removeAttribute(ATTRIBUTE_USER_REQUEST_SERVICE, session);
        LOG.trace(LOGGER_REMOVE_THE_ATTRIBUTE + ATTRIBUTE_USER_REQUEST_TARIFF);
        LOG.trace(LOGGER_REMOVE_THE_ATTRIBUTE + ATTRIBUTE_USER_REQUEST_SERVICE);

        LOG.debug("Command finished");
        return Path.MOVE_BY_REDIRECT_TO_ORDER_SUBMITTED_PAGE;
    }
}
