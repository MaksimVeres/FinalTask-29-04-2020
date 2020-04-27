package ua.nure.veres.summaryTask.web.command.create;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.ee.data.DataSynchronizer;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.service.ServiceCreateException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

/**
 * Create service command.
 */
public class CreateServiceCommand extends Command {

    private static final long serialVersionUID = -9115454878854454343L;

    private static final String PARAMETER_NAME = "name";

    private static final String ATTRIBUTE_USER = "user";

    private static final Logger LOG = Logger.getLogger(CreateServiceCommand.class);

    private transient DaoFactory serviceDaoFactory;

    private transient DataSynchronizer dataSynchronizer;

    static {
        BasicConfigurator.configure();
    }

    public CreateServiceCommand(DaoFactory serviceDaoFactory, DataSynchronizer dataSynchronizer) {
        this.serviceDaoFactory = serviceDaoFactory;
        this.dataSynchronizer = dataSynchronizer;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException, AppException {
        LOG.debug("Command starts");

        String name = getParameter(PARAMETER_NAME, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_NAME, name));

        if (!validateParameter(name)) {
            throw new ParameterException(PARAMETER_NAME);
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

        Dao<Service, Long> serviceDao = serviceDaoFactory.getDao();
        Service service = serviceDao.customReadOne("READ ONE name", name);
        LOG.trace("Found in DB (expected null): service --> " + service);

        if (service != null) {
            throw new ServiceCreateException(ServiceCreateException.MESSAGE_SUCH_SERVICE_ALREADY_IN_DB);
        }

        service = new Service();
        service.setName(name);
        serviceDao.create(service);
        LOG.info("Service created --> " + service);

        dataSynchronizer.synchronizeServices();

        LOG.debug("Command finished");
        return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SERVICE_LIST;
    }

}
