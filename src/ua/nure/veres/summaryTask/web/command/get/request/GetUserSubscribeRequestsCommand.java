package ua.nure.veres.summaryTask.web.command.get.request;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.db.bean.UserRequestBean;
import ua.nure.veres.summaryTask.db.entity.*;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Get user subscribe requests command.
 */
public class GetUserSubscribeRequestsCommand extends GetRequestsCommand {

    private static long serialVersionUID = 344466543242232332L;

    private static final String COMMAND_GET_USER_SUBSCRIBE_REQUESTS = "process?command=getUserSubscribeRequests";

    private static final String COMMAND_GET_USER_SUBSCRIBE_REQUESTS_SEARCH_ALL
            = COMMAND_GET_USER_SUBSCRIBE_REQUESTS + "&" + PARAMETER_SEARCH_ALL + "=" + VALUE_TRUE;

    private static final String COMMAND_GET_USER_SUBSCRIBE_REQUESTS_SEARCH_LOGIN
            = COMMAND_GET_USER_SUBSCRIBE_REQUESTS + "&" + PARAMETER_SEARCH_LOGIN + "=";

    private static final String ATTRIBUTE_REQUEST_SUBSCRIBE_BEANS = "requestSubscribeBeans";

    private static final String ATTRIBUTE_REPLY_SUBSCRIBE_REQUEST = "replySubscribeRequest";

    private static final Logger LOG = Logger.getLogger(GetUserSubscribeRequestsCommand.class);

    private transient UserOrderDaoFactory userOrderDaoFactory;

    private transient UserDaoFactory userDaoFactory;

    private transient TariffDaoFactory tariffDaoFactory;

    private transient ServiceDaoFactory serviceDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public GetUserSubscribeRequestsCommand(
            UserOrderDaoFactory userOrderDaoFactory,
            UserDaoFactory userDaoFactory,
            TariffDaoFactory tariffDaoFactory,
            ServiceDaoFactory serviceDaoFactory
    ) {
        this.userOrderDaoFactory = userOrderDaoFactory;
        this.userDaoFactory = userDaoFactory;
        this.tariffDaoFactory = tariffDaoFactory;
        this.serviceDaoFactory = serviceDaoFactory;
    }

    @Override
    protected String doGetCommand(HttpServletRequest request, HttpServletResponse response) throws AppException {
        HttpSession session = request.getSession();
        checkUserAccess(session);
        String searchAll = getParameter(PARAMETER_SEARCH_ALL, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_SEARCH_ALL, searchAll));

        Dao<UserOrder, Long> userOrderDao = userOrderDaoFactory.getDao();

        List<UserOrder> userOrders;
        String replyRequest;

        if (VALUE_TRUE.equals(searchAll)) {
            userOrders = userOrderDao.read();
            LOG.trace("Found in DB: all user orders, size --> " + userOrders.size());
            replyRequest = COMMAND_GET_USER_SUBSCRIBE_REQUESTS_SEARCH_ALL;
        } else {
            String userLogin = getParameter(PARAMETER_SEARCH_LOGIN, request);
            LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_SEARCH_LOGIN, userLogin));

            if (!validateParameter(userLogin)) {
                throw new ParameterException(PARAMETER_SEARCH_LOGIN);
            }

            replyRequest = COMMAND_GET_USER_SUBSCRIBE_REQUESTS_SEARCH_LOGIN + userLogin;
            userOrders = userOrderDao.customReadMany("READ MANY User.login", userLogin);
            LOG.trace(String.format("Found in DB: orders of user [%s], size --> %d", userLogin, userOrders.size()));
        }
        return doGetRequests(request, replyRequest, userOrders);
    }

    /**
     * Does command logic.
     *
     * @param request      HttpServletRequest
     * @param replyRequest address of the command to redirect
     * @param userOrders   List of UserOrder entities
     * @return result of execution
     */
    private String doGetRequests(HttpServletRequest request, String replyRequest
            , List<UserOrder> userOrders) throws AppException {
        ArrayList<UserRequestBean> requestBeans = new ArrayList<>();

        for (UserOrder userOrder : userOrders) {
            requestBeans.add(extractBean(userOrder));
        }
        LOG.info("Number of created requestBeans --> " + requestBeans.size());

        sortRequests(request, requestBeans);
        HttpSession session = request.getSession();
        setAttribute(ATTRIBUTE_REQUEST_SUBSCRIBE_BEANS, requestBeans, session);
        setAttribute(ATTRIBUTE_REPLY_SUBSCRIBE_REQUEST, replyRequest, session);
        LOG.trace(String.format("Set the session attribute: %s --> %s"
                , ATTRIBUTE_REQUEST_SUBSCRIBE_BEANS, requestBeans)
        );
        LOG.trace(String.format("Set the session attribute: %s --> %s"
                , ATTRIBUTE_REPLY_SUBSCRIBE_REQUEST, replyRequest)
        );

        return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SUBSCRIBE_REQUESTS_PAGE;
    }

    @Override
    <T extends Entity> UserRequestBean extractBean(T entity) throws AppException {
        UserOrder userOrder = (UserOrder) entity;

        Dao<User, Long> userDao = userDaoFactory.getDao();
        Dao<Tariff, Long> tariffDao = tariffDaoFactory.getDao();
        Dao<Service, Long> serviceDao = serviceDaoFactory.getDao();

        User user = userDao.read(userOrder.getUserId());
        LOG.trace("Found in DB: user --> " + user);

        Tariff tariff = tariffDao.read(userOrder.getTariffId());
        LOG.trace("Found in DB: tariff --> " + tariff);

        Service service = serviceDao.read(tariff.getServiceId());
        LOG.trace("Found in DB: service --> " + service);

        return new UserRequestBean(
                user, tariff, service, userOrder
        );
    }
}
