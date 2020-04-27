package ua.nure.veres.summaryTask.web.command.get.request;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.bean.UserRequestBean;
import ua.nure.veres.summaryTask.db.entity.*;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.web.command.get.GetCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Get requests abstract command.
 */
abstract class GetRequestsCommand extends GetCommand {
    static final String PARAMETER_SEARCH_ALL = "searchAll";

    static final String PARAMETER_SEARCH_LOGIN = "searchLogin";

    private static final String PARAMETER_SORT_COLUMN = "sortColumn";

    static final String VALUE_TRUE = "true";

    private static final String SORT_COLUMN_ID = "id";

    private static final String SORT_COLUMN_LOGIN = "login";

    private static final String SORT_COLUMN_FIRST_NAME = "firstName";

    private static final String SORT_COLUMN_LAST_NAME = "lastName";

    private static final String SORT_COLUMN_PHONE = "phone";

    private static final String SORT_COLUMN_SERVICE = "service";

    private static final String SORT_COLUMN_TARIFF = "tariff";

    private static final String SORT_COLUMN_COMMENT = "comment";

    private static final String SORT_COLUMN_REQUEST_DATE = "requestDate";

    private static final Logger LOG = Logger.getLogger(GetRequestsCommand.class);

    static {
        BasicConfigurator.configure();
    }

    /**
     * Sorts list of UserRequestBeans by specified comparator
     *
     * @param request      HttpServletRequest
     * @param requestBeans List to sort
     */
    void sortRequests(HttpServletRequest request, List<UserRequestBean> requestBeans) {
        String sortColumn = getParameter(PARAMETER_SORT_COLUMN, request);
        LOG.trace("Request parameter: " + PARAMETER_SORT_COLUMN + " --> " + sortColumn);

        if (sortColumn == null) {
            sortColumn = SORT_COLUMN_REQUEST_DATE;
        }

        Comparator<? super UserRequestBean> comparator;
        switch (sortColumn) {
            case SORT_COLUMN_ID:
                comparator = Comparator.comparing(UserRequestBean::getUserRequestId);
                break;
            case SORT_COLUMN_LOGIN:
                comparator = Comparator.comparing(UserRequestBean::getUserLogin);
                break;
            case SORT_COLUMN_FIRST_NAME:
                comparator = Comparator.comparing(UserRequestBean::getUserFirstName);
                break;
            case SORT_COLUMN_LAST_NAME:
                comparator = Comparator.comparing(UserRequestBean::getUserLastName);
                break;
            case SORT_COLUMN_PHONE:
                comparator = Comparator.comparing(UserRequestBean::getUserPhone);
                break;
            case SORT_COLUMN_SERVICE:
                comparator = Comparator.comparing(UserRequestBean::getServiceName);
                break;
            case SORT_COLUMN_TARIFF:
                comparator = Comparator.comparing(UserRequestBean::getTariffName);
                break;
            case SORT_COLUMN_COMMENT:
                comparator = Comparator.comparing(UserRequestBean::getComment);
                break;
            default:
                comparator = Comparator.comparing(UserRequestBean::getRequestDate);
                break;
        }
        requestBeans.sort(comparator);
    }

    /**
     * Extract an UserRequestBean from an entity.
     *
     * @param entity entity to extract
     * @return UserRequestBean
     */
    abstract <T extends Entity> UserRequestBean extractBean(T entity) throws AppException;

}
