package ua.nure.veres.summaryTask.web.command.submit.request;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.UserOrder;
import ua.nure.veres.summaryTask.db.entity.UserService;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.AttributeException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Date;

/**
 * Submit subscribe command.
 */
public class SubmitSubscribeRequestCommand extends SubmitRequestCommand {

    private static final long serialVersionUID = 54665878999932L;

    private static final String ATTRIBUTE_REPLY_SUBSCRIBE_REQUEST = "replySubscribeRequest";

    private static final String PARAMETER_ADDRESS = "address";

    private static final Logger LOG = Logger.getLogger(SubmitSubscribeRequestCommand.class);

    private transient UserOrderDaoFactory userOrderDaoFactory;

    private transient UserServiceDaoFactory userServiceDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public SubmitSubscribeRequestCommand(
            UserOrderDaoFactory userOrderDaoFactory,
            UserServiceDaoFactory userServiceDaoFactory
    ) {
        this.userOrderDaoFactory = userOrderDaoFactory;
        this.userServiceDaoFactory = userServiceDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        String requestId = getUserRequestId(request);

        String addressParameter = getParameter(PARAMETER_ADDRESS, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_ADDRESS, addressParameter));

        if (!validateParameter(addressParameter)) {
            throw new ParameterException(PARAMETER_ADDRESS);
        }

        long requestIdValue;
        try {
            requestIdValue = Long.parseLong(requestId);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParameterException(PARAMETER_USER_REQUEST_ID);
        }

        Dao<UserOrder, Long> userOrderDao = userOrderDaoFactory.getDao();
        UserOrder userOrder = userOrderDao.read(requestIdValue);
        LOG.trace("Found in DB: user order --> " + userOrder);

        if (userOrder == null) {
            throw new ParameterException(PARAMETER_USER_REQUEST_ID);
        }

        Dao<UserService, Long> userServiceDao = userServiceDaoFactory.getDao();
        UserService userService = new UserService();
        userService.setUserId(userOrder.getUserId());
        userService.setTariffId(userOrder.getTariffId());
        userService.setAddress(addressParameter);
        userService.setLastPaymentDate(new Date(System.currentTimeMillis()));
        userServiceDao.create(userService);
        LOG.info("User service created --> " + userService);
        userOrderDao.delete(userOrder);
        LOG.info("User subscribe request was submitted and deleted --> " + userOrder);

        HttpSession session = request.getSession();
        String replySubscribeRequest = (String) getAttribute(ATTRIBUTE_REPLY_SUBSCRIBE_REQUEST, session);
        LOG.trace(String.format("Session attribute: %s --> %s"
                , ATTRIBUTE_REPLY_SUBSCRIBE_REQUEST, replySubscribeRequest
        ));

        if (replySubscribeRequest == null) {
            throw new AttributeException(ATTRIBUTE_REPLY_SUBSCRIBE_REQUEST);
        }

        LOG.debug("Command finished");
        return replySubscribeRequest;

    }
}
