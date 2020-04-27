package ua.nure.veres.summaryTask.web.command.get.request;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userDescribe.UserDescribeDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
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
 * Get user describe requests command.
 */
public class GetUserDescribeRequestsCommand extends GetRequestsCommand {

    private static final long serialVersionUID = 176434876575432332L;

    private static final String COMMAND_GET_USER_DESCRIBE_REQUESTS_SEARCH_ALL
            = "process?command=getUserDescribeRequests&searchAll=true";
    private static final String COMMAND_GET_USER_DESCRIBE_REQUESTS_SEARCH_LOGIN
            = "process?command=getUserDescribeRequests&searchLogin=";

    private static final String ATTRIBUTE_REQUEST_DESCRIBE_BEANS = "requestDescribeBeans";

    private static final String ATTRIBUTE_REPLY_DESCRIBE_REQUEST = "replyDescribeRequest";

    private static final Logger LOG = Logger.getLogger(GetUserDescribeRequestsCommand.class);

    private transient UserDescribeDaoFactory userDescribeDaoFactory;

    private transient UserServiceDaoFactory userServiceDaoFactory;

    private transient UserDaoFactory userDaoFactory;

    private transient TariffDaoFactory tariffDaoFactory;

    private transient ServiceDaoFactory serviceDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public GetUserDescribeRequestsCommand(
            UserDescribeDaoFactory userDescribeDaoFactory,
            UserServiceDaoFactory userServiceDaoFactory,
            UserDaoFactory userDaoFactory,
            TariffDaoFactory tariffDaoFactory,
            ServiceDaoFactory serviceDaoFactory
    ) {
        this.userDescribeDaoFactory = userDescribeDaoFactory;
        this.userServiceDaoFactory = userServiceDaoFactory;
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

        Dao<UserDescribe, Long> userDescribeDao = userDescribeDaoFactory.getDao();

        List<UserDescribe> userDescribes;
        String replyRequest;

        if (VALUE_TRUE.equals(searchAll)) {
            userDescribes = userDescribeDao.read();
            LOG.trace("Found in DB: all user describes, size --> " + userDescribes.size());
            replyRequest = COMMAND_GET_USER_DESCRIBE_REQUESTS_SEARCH_ALL;
        } else {
            String userLogin = getParameter(PARAMETER_SEARCH_LOGIN, request);
            LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_SEARCH_LOGIN, userLogin));

            if (!validateParameter(userLogin)) {
                throw new ParameterException(userLogin);
            }

            replyRequest = COMMAND_GET_USER_DESCRIBE_REQUESTS_SEARCH_LOGIN + userLogin;
            userDescribes = userDescribeDao.customReadMany("READ MANY User.login", userLogin);
            LOG.trace(
                    String.format("Found in DB: describes of user [%s], size --> %d", userLogin, userDescribes.size())
            );
        }
        return doGetRequests(request, replyRequest, userDescribes);
    }

    /**
     * Does command logic.
     *
     * @param request       HttpServletRequest
     * @param replyRequest  address of the command to redirect
     * @param userDescribes List of UserDescribe entities
     * @return result of execution
     */
    private String doGetRequests(HttpServletRequest request, String replyRequest
            , List<UserDescribe> userDescribes) throws AppException {
        ArrayList<UserRequestBean> requestBeans = new ArrayList<>();

        for (UserDescribe userDescribe : userDescribes) {
            requestBeans.add(extractBean(userDescribe));
        }
        LOG.info("Number of created requestBeans --> " + requestBeans.size());

        sortRequests(request, requestBeans);
        HttpSession session = request.getSession();
        setAttribute(ATTRIBUTE_REQUEST_DESCRIBE_BEANS, requestBeans, session);
        setAttribute(ATTRIBUTE_REPLY_DESCRIBE_REQUEST, replyRequest, session);
        LOG.trace(String.format("Set the session attribute: %s --> %s"
                , ATTRIBUTE_REQUEST_DESCRIBE_BEANS, requestBeans));
        LOG.trace(String.format("Set the session attribute: %s --> %s"
                , ATTRIBUTE_REPLY_DESCRIBE_REQUEST, replyRequest));

        return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_DESCRIBE_REQUESTS_PAGE;
    }

    @Override
    <T extends Entity> UserRequestBean extractBean(T entity) throws AppException {
        UserDescribe userDescribe = (UserDescribe) entity;

        Dao<UserService, Long> userServiceDao = userServiceDaoFactory.getDao();
        Dao<User, Long> userDao = userDaoFactory.getDao();
        Dao<Tariff, Long> tariffDao = tariffDaoFactory.getDao();
        Dao<Service, Long> serviceDao = serviceDaoFactory.getDao();

        UserService userService = userServiceDao.read(userDescribe.getUserServiceId());
        LOG.trace("Found in DB: user service --> " + userService);

        User user = userDao.read(userService.getUserId());
        LOG.trace("Found in DB: user --> " + user);

        Tariff tariff = tariffDao.read(userService.getTariffId());
        LOG.trace("Found in DB: tariff --> " + tariff);

        Service service = serviceDao.read(tariff.getServiceId());
        LOG.trace("Found in DB: service --> " + service);

        return new UserRequestBean(
                user, tariff, service, userDescribe
        );
    }

}
