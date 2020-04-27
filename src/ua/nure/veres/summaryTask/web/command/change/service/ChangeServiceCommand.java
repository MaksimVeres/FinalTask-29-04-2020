package ua.nure.veres.summaryTask.web.command.change.service;

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
import ua.nure.veres.summaryTask.exception.service.ServiceUpdateException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.change.ChangeCommand;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

/**
 * Change service command.
 */
public class ChangeServiceCommand extends ChangeCommand {

    private static final long serialVersionUID = -2315466664345788956L;

    private static final String PARAMETER_ID = "id";

    private static final String PARAMETER_NAME = "name";

    private static final String ATTRIBUTE_REPLY_REQUEST = "replyRequest";

    private static final Logger LOG = Logger.getLogger(ChangeServiceCommand.class);

    private transient DaoFactory serviceDaoFactory;

    private transient DataSynchronizer dataSynchronizer;

    static {
        BasicConfigurator.configure();
    }

    public ChangeServiceCommand(DaoFactory serviceDaoFactory, DataSynchronizer dataSynchronizer) {
        this.serviceDaoFactory = serviceDaoFactory;
        this.dataSynchronizer = dataSynchronizer;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws IOException
            , ServletException, AppException {
        LOG.debug("Command starts");

        String id = getParameter(PARAMETER_ID, request);
        String name = getParameter(PARAMETER_NAME, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_ID, id));
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_NAME, name));

        if (!validateParameter(id)) {
            throw new ParameterException(PARAMETER_ID);
        }

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
            throw new ServiceUpdateException(ServiceUpdateException.MESSAGE_SUCH_NAME_ALREADY_IN_DB);
        }

        try {
            long idValue = Long.parseLong(id);
            service = serviceDao.read(idValue);
            LOG.trace("Found in DB: service --> " + service);

            if (service == null) {
                throw new ServiceUpdateException(ServiceUpdateException.MESSAGE_NO_SUCH_SERVICE_IN_DB);
            }

            service.setName(name);
            serviceDao.update(service);
            LOG.info(String.format("Name of service with id [%s] was changed to %s"
                    , service.getId(), service.getName())
            );

            dataSynchronizer.synchronizeServices();

        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParameterException(PARAMETER_ID);
        }

        String replyRequest = (String) getAttribute(ATTRIBUTE_REPLY_REQUEST, session);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_REPLY_REQUEST, replyRequest));

        LOG.debug("Command finished");

        if (replyRequest != null) {
            return replyRequest;
        }

        return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SERVICE_LIST;
    }

}
