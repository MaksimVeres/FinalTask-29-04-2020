package ua.nure.veres.summaryTask.web.command.user;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserService;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;

/**
 * Update user services command.
 */
public class UpdateUserServicesCommand extends Command {

    private static final long serialVersionUID = -468781211111556L;

    private static final String ATTRIBUTE_USER = "user";

    private static final String ATTRIBUTE_USER_SERVICE_MAP = "userServiceMap";

    private static final Logger LOG = Logger.getLogger(UpdateUserServicesCommand.class);

    private transient ServiceDaoFactory serviceDaoFactory;

    private transient TariffDaoFactory tariffDaoFactory;

    private transient UserServiceDaoFactory userServiceDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public UpdateUserServicesCommand(
            ServiceDaoFactory serviceDaoFactory,
            TariffDaoFactory tariffDaoFactory,
            UserServiceDaoFactory userServiceDaoFactory
    ) {
        this.serviceDaoFactory = serviceDaoFactory;
        this.tariffDaoFactory = tariffDaoFactory;
        this.userServiceDaoFactory = userServiceDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        HttpSession session = request.getSession();

        User user = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_USER, user));

        if (user == null) {
            throw new CommandAccessException(CommandAccessException.MESSAGE_UNAUTHORIZED);
        }

        TreeMap<UserService, Map<Tariff, Service>> userServiceTariffMap = new TreeMap<>();

        Dao<UserService, Long> userServiceDao = userServiceDaoFactory.getDao();
        List<UserService> userServices = userServiceDao.customReadMany("READ MANY User.login", user.getLogin());
        LOG.trace(String.format("Found in DB: userServices of user [%s] --> %s", user.getLogin(), userServices));

        userServices.sort(Comparator.comparing(UserService::getTariffId));

        Dao<Tariff, Long> tariffDao = tariffDaoFactory.getDao();
        Dao<Service, Long> serviceDao = serviceDaoFactory.getDao();

        for (UserService userService : userServices) {
            Map<Tariff, Service> tariffServiceMap = new HashMap<>();

            Tariff tariff = tariffDao.read(userService.getTariffId());
            LOG.trace("Found in DB: tariff --> " + tariff);

            Service service = serviceDao.read(tariff.getServiceId());
            LOG.trace("Found in DB: service --> " + service);

            tariffServiceMap.put(tariff, service);
            userServiceTariffMap.put(userService, tariffServiceMap);
        }

        setAttribute(ATTRIBUTE_USER_SERVICE_MAP, userServiceTariffMap, session);
        LOG.trace(String.format("Set the session attribute: %s --> %s"
                , ATTRIBUTE_USER_SERVICE_MAP, userServiceTariffMap));

        LOG.debug("Command finished");
        return Path.MOVE_BY_FORWARD_TO_PERSONAL_CABINET_SERVICES_PAGE;
    }
}
