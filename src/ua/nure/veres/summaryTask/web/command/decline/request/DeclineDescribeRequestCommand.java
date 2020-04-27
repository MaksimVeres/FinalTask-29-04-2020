package ua.nure.veres.summaryTask.web.command.decline.request;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.UserDescribe;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.AttributeException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Decline describe request command.
 */
public class DeclineDescribeRequestCommand extends DeclineRequestCommand {

    private static final long serialVersionUID = 676434343242232332L;

    private static final String ATTRIBUTE_REPLY_DESCRIBE_REQUEST = "replyDescribeRequest";

    private static final Logger LOG = Logger.getLogger(DeclineDescribeRequestCommand.class);

    private transient DaoFactory userDescribeDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public DeclineDescribeRequestCommand(DaoFactory userDescribeDaoFactory) {
        this.userDescribeDaoFactory = userDescribeDaoFactory;
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

        Dao<UserDescribe, Long> userDescribeDao = userDescribeDaoFactory.getDao();

        UserDescribe userDescribe = userDescribeDao.read(userRequestIdValue);
        LOG.trace("Found in DB: user describe --> " + userDescribe);

        if (userDescribe == null) {
            throw new ParameterException(PARAMETER_USER_REQUEST_ID);
        }

        userDescribeDao.delete(userDescribe);
        LOG.info("User describe request was declined and deleted --> " + userDescribe);

        HttpSession session = request.getSession();

        String replyDescribeRequest = (String) getAttribute(ATTRIBUTE_REPLY_DESCRIBE_REQUEST, session);
        LOG.trace("Session attribute: " + ATTRIBUTE_REPLY_DESCRIBE_REQUEST + " --> " + replyDescribeRequest);

        if (replyDescribeRequest == null) {
            throw new AttributeException(ATTRIBUTE_REPLY_DESCRIBE_REQUEST);
        }

        LOG.debug("Command finished");

        return replyDescribeRequest;

    }
}
