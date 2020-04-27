package ua.nure.veres.summaryTask.web.command.create;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.tariff.TariffCreateException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

/**
 * Create tariff command.
 */
public class CreateTariffCommand extends Command {

    private static final long serialVersionUID = 111654645873333L;

    private static final String PARAMETER_NAME = "name";

    private static final String PARAMETER_CONNECTION_PAYMENT = "connectionPayment";

    private static final String PARAMETER_MONTH_PAYMENT = "monthPayment";

    private static final String PARAMETER_FEATURE = "feature";

    private static final String PARAMETER_SERVICE_ID = "serviceId";

    private static final String ATTRIBUTE_USER = "user";

    private static final Logger LOG = Logger.getLogger(CreateTariffCommand.class);

    private static final String LOG_REQUESTED_PARAMETER = "Request parameter: %s --> %s";

    private transient TariffDaoFactory tariffDaoFactory;

    private transient ServiceDaoFactory serviceDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public CreateTariffCommand(TariffDaoFactory tariffDaoFactory, ServiceDaoFactory serviceDaoFactory) {
        this.tariffDaoFactory = tariffDaoFactory;
        this.serviceDaoFactory = serviceDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
            , AppException {

        LOG.debug("Command starts");

        String name = getParameter(PARAMETER_NAME, request);
        String connectionPayment = getParameter(PARAMETER_CONNECTION_PAYMENT, request);
        String monthPayment = getParameter(PARAMETER_MONTH_PAYMENT, request);
        String feature = getParameter(PARAMETER_FEATURE, request);
        String serviceId = getParameter(PARAMETER_SERVICE_ID, request);
        LOG.trace(String.format(LOG_REQUESTED_PARAMETER, PARAMETER_NAME, name));
        LOG.trace(String.format(LOG_REQUESTED_PARAMETER, PARAMETER_CONNECTION_PAYMENT, connectionPayment));
        LOG.trace(String.format(LOG_REQUESTED_PARAMETER, PARAMETER_MONTH_PAYMENT, monthPayment));
        LOG.trace(String.format(LOG_REQUESTED_PARAMETER, PARAMETER_FEATURE, feature));
        LOG.trace(String.format(LOG_REQUESTED_PARAMETER, PARAMETER_SERVICE_ID, serviceId));

        validateParameters(name, connectionPayment, monthPayment, feature, serviceId);

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

        try {
            double connectionPaymentValue = Double.parseDouble(connectionPayment);
            double monthPaymentValue = Double.parseDouble(monthPayment);
            long serviceIdValue = Long.parseLong(serviceId);

            Dao<Tariff, Long> tariffDao = tariffDaoFactory.getDao();

            Tariff tariff = tariffDao.customReadOne("READ ONE name", name);
            LOG.trace("Found in DB (expected null): tariff --> " + tariff);

            if (tariff != null) {
                throw new TariffCreateException(TariffCreateException.MESSAGE_SUCH_TARIFF_ALREADY_IN_DB);
            }

            Dao<Service, Long> serviceDao = serviceDaoFactory.getDao();
            Service service = serviceDao.read(serviceIdValue);
            LOG.trace("Found in DB: service --> " + service);

            if (service == null) {
                throw new TariffCreateException(TariffCreateException.MESSAGE_NO_SUCH_SERVICE_IN_DB);
            }

            tariff = new Tariff();
            tariff.setName(name);
            tariff.setConnectionPayment(connectionPaymentValue);
            tariff.setMonthPayment(monthPaymentValue);
            tariff.setFeature(feature);
            tariff.setServiceId(serviceIdValue);
            tariffDao.create(tariff);
            LOG.info("Tariff created --> " + tariff);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new TariffCreateException(TariffCreateException.MESSAGE_NOT_NUMBER_PARAMETER_WAS_GIVEN);
        }

        LOG.debug("Command finished");
        return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_TARIFF_LIST;
    }

    /**
     * Validates required parameters.
     *
     * @param name              name parameter
     * @param connectionPayment connectionPayment parameter
     * @param monthPayment      monthPayment parameter
     * @param feature           feature parameter
     * @param serviceId         serviceId parameter
     * @throws ParameterException If some of parameter was invalidated
     */
    private void validateParameters(String name, String connectionPayment
            , String monthPayment, String feature, String serviceId) throws ParameterException {
        if (!validateParameter(name)) {
            throw new ParameterException(PARAMETER_NAME);
        }

        if (!validateParameter(connectionPayment)) {
            throw new ParameterException(PARAMETER_CONNECTION_PAYMENT);
        }

        if (!validateParameter(monthPayment)) {
            throw new ParameterException(PARAMETER_MONTH_PAYMENT);
        }

        if (!validateParameter(feature)) {
            throw new ParameterException(PARAMETER_FEATURE);
        }

        if (!validateParameter(serviceId)) {
            throw new ParameterException(PARAMETER_SERVICE_ID);
        }
    }
}
