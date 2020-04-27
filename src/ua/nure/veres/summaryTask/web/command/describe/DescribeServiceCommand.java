package ua.nure.veres.summaryTask.web.command.describe;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userDescribe.UserDescribeDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserDescribe;
import ua.nure.veres.summaryTask.db.entity.UserService;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.AttributeException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Date;

/**
 * Describe service command.
 */
public class DescribeServiceCommand extends Command {

    private static final long serialVersionUID = -46654367768325432L;

    private static final String PARAMETER_PHONE = "phone";

    private static final String PARAMETER_COMMENT = "comment";

    private static final String ATTRIBUTE_USER = "user";

    private static final String ATTRIBUTE_USER_DESCRIBE_USER_SERVICE_ID = "userDescribeUserServiceId";

    private static final String ATTRIBUTE_USER_REQUEST_TARIFF = "userRequestTariff";

    private static final String ATTRIBUTE_USER_REQUEST_SERVICE = "userRequestService";

    private static final Logger LOG = Logger.getLogger(DescribeServiceCommand.class);

    private static final String LOGGER_REMOVE_THE_ATTRIBUTE = "Remove the session attribute: ";

    private transient UserDescribeDaoFactory userDescribeDaoFactory;

    private transient UserServiceDaoFactory userServiceDaoFactory;

    private transient UserDaoFactory userDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public DescribeServiceCommand(
            UserDescribeDaoFactory userDescribeDaoFactory,
            UserServiceDaoFactory userServiceDaoFactory,
            UserDaoFactory userDaoFactory
    ) {
        this.userDescribeDaoFactory = userDescribeDaoFactory;
        this.userServiceDaoFactory = userServiceDaoFactory;
        this.userDaoFactory = userDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        HttpSession session = request.getSession();

        String attributeId = (String) getAttribute(ATTRIBUTE_USER_DESCRIBE_USER_SERVICE_ID, session);
        LOG.trace("Session attribute: " + ATTRIBUTE_USER_DESCRIBE_USER_SERVICE_ID + " --> " + attributeId);

        if (!validateParameter(attributeId)) {
            throw new AttributeException(ATTRIBUTE_USER_DESCRIBE_USER_SERVICE_ID);
        }

        long describeUserServiceId;
        try {
            describeUserServiceId = Long.parseLong(attributeId);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AttributeException(ATTRIBUTE_USER_DESCRIBE_USER_SERVICE_ID);
        }

        String phone = getParameter(PARAMETER_PHONE, request);
        String comment = getParameter(PARAMETER_COMMENT, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_PHONE, phone));
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_COMMENT, comment));

        if (!validateParameter(phone)) {
            throw new ParameterException(PARAMETER_PHONE);
        }

        User sessionUser = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace("Session attribute: user --> " + sessionUser);

        if (sessionUser == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        Dao<UserService, Long> userServiceDao = userServiceDaoFactory.getDao();
        UserService userService = userServiceDao.read(describeUserServiceId);

        if (userService == null) {
            throw new UserUpdateException(UserUpdateException.MESSAGE_NO_SUCH_USER_IN_DB);
        }

        Dao<User, Long> userDao = userDaoFactory.getDao();
        User dbUser = userDao.read(userService.getUserId());

        if (!sessionUser.getLogin().equals(dbUser.getLogin())) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_FORBIDDEN);
        }

        UserDescribe userDescribe = new UserDescribe();
        userDescribe.setUserServiceId(describeUserServiceId);
        userDescribe.setComment(comment);
        userDescribe.setUserPhone(phone);
        userDescribe.setDescribeDate(new Date(System.currentTimeMillis()));

        Dao<UserDescribe, Long> userDescribeDao = userDescribeDaoFactory.getDao();
        userDescribeDao.create(userDescribe);
        LOG.info("User describe created --> " + userDescribe);

        removeAttribute(ATTRIBUTE_USER_DESCRIBE_USER_SERVICE_ID, session);
        removeAttribute(ATTRIBUTE_USER_REQUEST_TARIFF, session);
        removeAttribute(ATTRIBUTE_USER_REQUEST_SERVICE, session);
        LOG.trace(String.format("%s%s", LOGGER_REMOVE_THE_ATTRIBUTE, ATTRIBUTE_USER_DESCRIBE_USER_SERVICE_ID));
        LOG.trace(String.format("%s%s", LOGGER_REMOVE_THE_ATTRIBUTE, ATTRIBUTE_USER_REQUEST_TARIFF));
        LOG.trace(String.format("%s%s", LOGGER_REMOVE_THE_ATTRIBUTE, ATTRIBUTE_USER_REQUEST_SERVICE));
        LOG.debug("Command finished");

        return Path.MOVE_BY_REDIRECT_TO_DESCRIBE_SUBMITTED_PAGE;
    }
}
