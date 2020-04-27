package ua.nure.veres.summaryTask.web.command.forward.tariff;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.forward.ForwardCommandException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.tariff.TariffUpdateException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.forward.ForwardCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * To tariff change form command.
 */
public class ToTariffChangeFormCommand extends ForwardCommand {

    private static final long serialVersionUID = -100045677659999L;

    private static final String PARAMETER_TARIFF_ID = "id";

    private static final String ATTRIBUTE_USER = "user";

    private static final String ATTRIBUTE_CHANGING_TARIFF = "changingTariff";

    private static final Logger LOG = Logger.getLogger(ToTariffChangeFormCommand.class);

    private transient DaoFactory tariffDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public ToTariffChangeFormCommand(DaoFactory tariffDaoFactory) {
        this.tariffDaoFactory = tariffDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        validateForwardMethod(request);
        String forwardMethod = getForwardMethod(request);
        if (!forwardMethod.equals(FORWARD_METHOD_FORWARD)) {
            throw new ForwardCommandException(ForwardCommandException.MESSAGE_NO_REDIRECT_METHOD_SUPPORTED);
        }

        String id = getParameter(PARAMETER_TARIFF_ID, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_TARIFF_ID, id));

        if (!validateParameter(id)) {
            throw new ParameterException(PARAMETER_TARIFF_ID);
        }

        HttpSession session = request.getSession();
        User user = (User) getAttribute(ATTRIBUTE_USER, session);
        LOG.trace("Session attribute: user --> " + user);

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
            throw new ParameterException(PARAMETER_TARIFF_ID);
        }

        Dao<Tariff, Long> tariffDao = tariffDaoFactory.getDao();
        Tariff tariff = tariffDao.read(idValue);
        LOG.trace("Found in DB: tariff --> " + tariff);

        if (tariff == null) {
            throw new TariffUpdateException(TariffUpdateException.MESSAGE_NO_SUCH_TARIFF_IN_DB);
        }

        setAttribute(ATTRIBUTE_CHANGING_TARIFF, tariff, session);
        LOG.info(String.format("Set session attribute %s --> %s", ATTRIBUTE_CHANGING_TARIFF, tariff));


        LOG.debug("Command finished");
        return Path.MOVE_BY_FORWARD_TO_ADMINISTRATION_CHANGE_TARIFF_FORM;
    }
}
