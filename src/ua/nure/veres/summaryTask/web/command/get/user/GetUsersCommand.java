package ua.nure.veres.summaryTask.web.command.get.user;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.bean.UserBean;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.get.GetCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Get users command.
 */
public class GetUsersCommand extends GetCommand {

    private static final long serialVersionUID = 888555432434554311L;

    private static final String PARAMETER_SORT_COLUMN = "sortColumn";

    private static final String PARAMETER_SEARCH_ALL = "searchAll";

    private static final String PARAMETER_SEARCH_LOGIN = "searchLogin";

    private static final String ATTRIBUTE_REQUESTED_USERS = "requestedUsers";

    private static final String ATTRIBUTE_REPLY_REQUEST = "replyRequest";

    private static final String SORT_COLUMN_ID = "id";

    private static final String SORT_COLUMN_LOGIN = "login";

    private static final String SORT_COLUMN_FIRST_NAME = "firstName";

    private static final String SORT_COLUMN_LAST_NAME = "lastName";

    private static final String SORT_COLUMN_STATUS_ID = "statusId";

    private static final String SORT_COLUMN_ROLE_ID = "roleId";

    private static final String SORT_COLUMN_ACCOUNT_STATE = "accountState";

    private static final String VALUE_TRUE = "true";

    private static final String COMMAND_GET_USERS = "process?command=getUsers";

    private static final String COMMAND_GET_USERS_SEARCH_ALL
            = COMMAND_GET_USERS + "&" + PARAMETER_SEARCH_ALL + "=" + VALUE_TRUE;

    private static final String COMMAND_GET_USERS_SEARCH_LOGIN
            = COMMAND_GET_USERS + "&" + PARAMETER_SEARCH_LOGIN + "=";

    private static final Logger LOG = Logger.getLogger(GetUsersCommand.class);

    private static final String LOG_REQUEST_PARAMETER = "Request parameter: %s --> %s";

    private transient DaoFactory userDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public GetUsersCommand(DaoFactory userDaoFactory) {
        this.userDaoFactory = userDaoFactory;
    }

    @Override
    protected String doGetCommand(HttpServletRequest request, HttpServletResponse response) throws AppException {
        HttpSession session = request.getSession();
        checkUserAccess(session);
        String replyRequest;

        String searchAll = getParameter(PARAMETER_SEARCH_ALL, request);
        LOG.trace(String.format(LOG_REQUEST_PARAMETER, PARAMETER_SEARCH_ALL, searchAll));

        Dao<User, Long> userDao = userDaoFactory.getDao();

        List<User> users;
        if (VALUE_TRUE.equals(searchAll)) {
            users = userDao.read();

            LOG.trace("Found in DB: users, size --> " + users.size());

            replyRequest = COMMAND_GET_USERS_SEARCH_ALL;
        } else {
            String login = getParameter(PARAMETER_SEARCH_LOGIN, request);
            LOG.trace(String.format(LOG_REQUEST_PARAMETER, PARAMETER_SEARCH_LOGIN, login));

            if (!validateParameter(login)) {
                throw new ParameterException(PARAMETER_SEARCH_LOGIN);
            }

            User user = userDao.customReadOne("READ ONE login", login);
            LOG.trace("Found in DB: user --> " + user);

            users = new ArrayList<>();
            if (user != null) {
                users.add(user);
            }

            replyRequest = COMMAND_GET_USERS_SEARCH_LOGIN + login;
        }

        ArrayList<UserBean> userBeans = new ArrayList<>();
        for (User user : users) {
            userBeans.add(new UserBean(user));
        }

        sortUsers(userBeans, request);

        setAttribute(ATTRIBUTE_REQUESTED_USERS, userBeans, session);
        setAttribute(ATTRIBUTE_REPLY_REQUEST, replyRequest, session);
        LOG.trace(String.format("Set the session attribute: %s, size --> %s"
                , ATTRIBUTE_REQUESTED_USERS, userBeans.size()));
        LOG.trace(String.format("Set the session attribute: %s --> %s"
                , ATTRIBUTE_REPLY_REQUEST, replyRequest));

        return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_USER_LIST;
    }

    /**
     * Sorts an user beans by a sortColumn request parameter.
     *
     * @param userBeans UserBean list
     * @param request   HttpServletRequest
     */
    private void sortUsers(List<UserBean> userBeans, HttpServletRequest request) {
        String sortColumn = getParameter(PARAMETER_SORT_COLUMN, request);
        LOG.trace(String.format(LOG_REQUEST_PARAMETER, PARAMETER_SORT_COLUMN, sortColumn));

        if (sortColumn == null) {
            sortColumn = SORT_COLUMN_ID;
        }

        Comparator<? super UserBean> comparator;
        switch (sortColumn) {
            case SORT_COLUMN_LOGIN:
                comparator = Comparator.comparing(UserBean::getLogin);
                break;
            case SORT_COLUMN_FIRST_NAME:
                comparator = Comparator.comparing(UserBean::getFirstName);
                break;
            case SORT_COLUMN_LAST_NAME:
                comparator = Comparator.comparing(UserBean::getLastName);
                break;
            case SORT_COLUMN_ROLE_ID:
                comparator = Comparator.comparing(UserBean::getRole);
                break;
            case SORT_COLUMN_STATUS_ID:
                comparator = Comparator.comparing(UserBean::getStatus);
                break;
            case SORT_COLUMN_ACCOUNT_STATE:
                comparator = Comparator.comparing(UserBean::getAccountState);
                break;
            default:
                comparator = Comparator.comparing(UserBean::getId);
                break;
        }
        userBeans.sort(comparator);
    }

}
