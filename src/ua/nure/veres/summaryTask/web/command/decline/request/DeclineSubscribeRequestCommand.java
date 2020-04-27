package ua.nure.veres.summaryTask.web.command.decline.request;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserOrder;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.AttributeException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Decline subscribe request command.
 */
public class DeclineSubscribeRequestCommand extends DeclineRequestCommand {

    private static final long serialVersionUID = -4324343242232332L;

    private static final String ATTRIBUTE_REPLY_SUBSCRIBE_REQUEST = "replySubscribeRequest";

    private static final Logger LOG = Logger.getLogger(DeclineSubscribeRequestCommand.class);

    private transient UserOrderDaoFactory userOrderDaoFactory;

    private transient TariffDaoFactory tariffDaoFactory;

    private transient UserDaoFactory userDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public DeclineSubscribeRequestCommand(
            UserOrderDaoFactory userOrderDaoFactory,
            TariffDaoFactory tariffDaoFactory,
            UserDaoFactory userDaoFactory) {
        this.userOrderDaoFactory = userOrderDaoFactory;
        this.tariffDaoFactory = tariffDaoFactory;
        this.userDaoFactory = userDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        String userRequestId = prepareToRequest(request);

        long userRequestIdValue;
        try {
            userRequestIdValue = Long.parseLong(userRequestId);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParameterException(PARAMETER_USER_REQUEST_ID);
        }

        Dao<UserOrder, Long> userOrderDao = userOrderDaoFactory.getDao();
        Dao<Tariff, Long> tariffDao = tariffDaoFactory.getDao();
        Dao<User, Long> userDao = userDaoFactory.getDao();

        UserOrder userOrder = userOrderDao.read(userRequestIdValue);
        LOG.trace("Found in DB: user order --> " + userOrder);

        if (userOrder == null) {
            throw new ParameterException(PARAMETER_USER_REQUEST_ID);
        }

        Tariff tariff = tariffDao.read(userOrder.getTariffId());
        LOG.trace("Found in DB: tariff --> " + tariff);

        if (tariff == null) {
            throw new ParameterException(PARAMETER_USER_REQUEST_ID);
        }

        double refund = tariff.getConnectionPayment();
        User customer = userDao.read(userOrder.getUserId());
        LOG.trace("Found in DB: user --> " + customer);

        if (customer == null) {
            throw new ParameterException(PARAMETER_USER_REQUEST_ID);
        }

        customer.setAccountState(customer.getAccountState() + refund);
        userDao.update(customer);
        LOG.info(String.format(
                "Account of user with login [%s] was replenished with an amount of --> %s, new account state --> %s"
                , customer.getLogin(), refund, customer.getAccountState()
        ));

        userOrderDao.delete(userOrder);
        LOG.info("User subscribe request was declined and deleted --> " + userOrder);

        HttpSession session = request.getSession();
        String replySubscribeRequest = (String) getAttribute(ATTRIBUTE_REPLY_SUBSCRIBE_REQUEST, session);
        LOG.trace("Session attribute: " + ATTRIBUTE_REPLY_SUBSCRIBE_REQUEST + " --> " + replySubscribeRequest);

        if (replySubscribeRequest == null) {
            throw new AttributeException(ATTRIBUTE_REPLY_SUBSCRIBE_REQUEST);
        }

        LOG.debug("Command finished");
        return replySubscribeRequest;

    }
}
