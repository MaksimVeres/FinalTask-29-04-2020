package ua.nure.veres.summaryTask.web.command.delete;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.userDescribe.UserDescribeDaoFactory;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.entity.*;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Delete tariff command.
 */
public class DeleteTariffCommand extends Command {

    private static final long serialVersionUID = 152444215000000L;

    private static final String PARAMETER_ID = "id";

    private static final String ATTRIBUTE_USER = "user";

    private static final String ATTRIBUTE_REPLY_REQUEST = "replyRequest";

    private static final Logger LOG = Logger.getLogger(DeleteTariffCommand.class);

    private transient TariffDaoFactory tariffDaoFactory;

    private transient UserDescribeDaoFactory userDescribeDaoFactory;

    private transient UserOrderDaoFactory userOrderDaoFactory;

    private transient UserServiceDaoFactory userServiceDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public DeleteTariffCommand(TariffDaoFactory tariffDaoFactory,
                               UserDescribeDaoFactory userDescribeDaoFactory,
                               UserOrderDaoFactory userOrderDaoFactory,
                               UserServiceDaoFactory userServiceDaoFactory
    ) {
        this.tariffDaoFactory = tariffDaoFactory;
        this.userDescribeDaoFactory = userDescribeDaoFactory;
        this.userOrderDaoFactory = userOrderDaoFactory;
        this.userServiceDaoFactory = userServiceDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
            , AppException {
        LOG.debug("Command starts");

        String id = getParameter(PARAMETER_ID, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_ID, id));

        if (!validateParameter(id)) {
            throw new ParameterException(PARAMETER_ID);
        }

        HttpSession session = request.getSession();
        User user = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_USER, user));

        if (user == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        LOG.info(String.format("User %s logged as %s"
                , user, Role.getRole(user).toString().toLowerCase(Locale.getDefault()))
        );

        if (!checkAccess(user, Role.ADMIN)) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_FORBIDDEN);
        }

        long idValue;
        try {
            idValue = Long.parseLong(id);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParameterException(PARAMETER_ID);
        }

        Dao<Tariff, Long> tariffDao = tariffDaoFactory.getDao();
        Tariff tariff = tariffDao.read(idValue);
        LOG.trace("Found in DB: tariff --> " + tariff);

        if (tariff == null) {
            LOG.debug("Command finished");
            return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_TARIFF_LIST;
        }

        Dao<UserDescribe, Long> userDescribeDao = userDescribeDaoFactory.getDao();
        Dao<UserOrder, Long> userOrderDao = userOrderDaoFactory.getDao();
        Dao<UserService, Long> userServiceDao = userServiceDaoFactory.getDao();

        List<UserDescribe> userDescribes = userDescribeDao.customReadMany("READ MANY Tariff.id", idValue);
        LOG.trace("Found in DB: linked user describes, size --> " + userDescribes.size());
        for (UserDescribe userDescribe : userDescribes) {
            userDescribeDao.delete(userDescribe);
        }
        LOG.info(String.format("%s linked user describes to tariff [id = %s] was deleted"
                , userDescribes.size(), idValue));

        List<UserOrder> userOrders = userOrderDao.customReadMany("READ MANY tariffId", idValue);
        LOG.trace("Found in DB: linked user orders, size --> " + userOrders.size());
        for (UserOrder userOrder : userOrders) {
            userOrderDao.delete(userOrder);
        }
        LOG.info(String.format("%s linked user orders to tariff [id = %s] was deleted"
                , userOrders.size(), idValue));

        List<UserService> userServices = userServiceDao.customReadMany("READ MANY tariffId", idValue);
        LOG.trace("Found in DB: linked user services, size --> " + userServices.size());
        for (UserService userService : userServices) {
            userServiceDao.delete(userService);
        }
        LOG.info(String.format("%s linked user services to tariff [id = %s] was deleted"
                , userServices.size(), idValue));

        tariffDao.delete(tariff);
        LOG.info("Tariff was deleted --> " + tariff);


        String replyRequest = (String) getAttribute(ATTRIBUTE_REPLY_REQUEST, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_REPLY_REQUEST, replyRequest));

        if (replyRequest != null) {
            return replyRequest;
        }

        LOG.debug("Command finished");
        return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_TARIFF_LIST;
    }


}
