package ua.nure.veres.summaryTask.web.command.submit.request;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.userDescribe.UserDescribeDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.UserDescribe;
import ua.nure.veres.summaryTask.db.entity.UserService;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.AttributeException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Submit describe request command.
 */
public class SubmitDescribeRequestCommand extends SubmitRequestCommand {

    private static final long serialVersionUID = -35467888885453425L;

    private static final String ATTRIBUTE_REPLY_DESCRIBE_REQUEST = "replyDescribeRequest";

    private static final Logger LOG = Logger.getLogger(SubmitDescribeRequestCommand.class);

    private transient UserServiceDaoFactory userServiceDaoFactory;

    private transient UserDescribeDaoFactory userDescribeDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public SubmitDescribeRequestCommand(
            UserServiceDaoFactory userServiceDaoFactory,
            UserDescribeDaoFactory userDescribeDaoFactory
    ) {
        this.userServiceDaoFactory = userServiceDaoFactory;
        this.userDescribeDaoFactory = userDescribeDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");
        String userRequestId = getUserRequestId(request);

        long userRequestIdValue;
        try {
            userRequestIdValue = Long.parseLong(userRequestId);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParameterException(PARAMETER_USER_REQUEST_ID);
        }

        Dao<UserDescribe, Long> userDescribeDao = userDescribeDaoFactory.getDao();
        UserDescribe userDescribe = userDescribeDao.read(userRequestIdValue);
        LOG.trace("Found in DB: user describe --> " + userDescribe);

        if (userDescribe == null) {
            throw new ParameterException(PARAMETER_USER_REQUEST_ID);
        }

        Dao<UserService, Long> userServiceDao = userServiceDaoFactory.getDao();
        UserService userService = userServiceDao.read(userDescribe.getUserServiceId());
        LOG.trace("Found in DB: userService --> " + userService);

        userServiceDao.delete(userService);
        LOG.info("User service was described and deleted --> " + userDescribe);

        userDescribeDao.delete(userDescribe);
        LOG.info("User describe request was submitted and deleted --> " + userDescribe);

        HttpSession session = request.getSession();
        String replyDescribeRequest = (String) getAttribute(ATTRIBUTE_REPLY_DESCRIBE_REQUEST, session);
        LOG.trace(String.format("Session attribute: %s --> %s"
                , ATTRIBUTE_REPLY_DESCRIBE_REQUEST, replyDescribeRequest
        ));

        if (replyDescribeRequest == null) {
            throw new AttributeException(ATTRIBUTE_REPLY_DESCRIBE_REQUEST);
        }

        LOG.debug("Command finished");
        return replyDescribeRequest;

    }
}
