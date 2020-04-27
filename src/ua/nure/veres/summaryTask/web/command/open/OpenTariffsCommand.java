package ua.nure.veres.summaryTask.web.command.open;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;

/**
 * Open tariffs command.
 */
public class OpenTariffsCommand extends Command {

    private static final long serialVersionUID = 79023323426213L;

    private static final String PARAMETER_SERVICE_ID = "serviceId";

    private static final String PARAMETER_SORT = "sort";

    private static final String ATTRIBUTE_REQUESTED_TARIFFS = "requestedTariffs";

    private static final String ATTRIBUTE_REQUESTED_TARIFFS_LINKED_SERVICE = "requestedTariffsLinkedService";

    private static final String SORT_TYPE_NAME_ASC = "name";

    private static final String SORT_TYPE_MONTH_PAYMENT_ASC = "monthPayment";

    private static final String SORT_TYPE_CONNECTION_PAYMENT_ASC = "connectionPayment";

    private static final String SORT_TYPE_NAME_DESC = "nameDESC";

    private static final String SORT_TYPE_MONTH_PAYMENT_DESC = "monthPaymentDESC";

    private static final String SORT_TYPE_CONNECTION_PAYMENT_DESC = "connectionPaymentDESC";

    private static final Logger LOG = Logger.getLogger(OpenTariffsCommand.class);

    private transient TariffDaoFactory tariffDaoFactory;

    private transient ServiceDaoFactory serviceDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public OpenTariffsCommand(TariffDaoFactory tariffDaoFactory,
                              ServiceDaoFactory serviceDaoFactory) {
        this.tariffDaoFactory = tariffDaoFactory;
        this.serviceDaoFactory = serviceDaoFactory;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        String serviceParameter = getParameter(PARAMETER_SERVICE_ID, request);
        LOG.trace("Request parameter: " + PARAMETER_SERVICE_ID + " --> " + serviceParameter);

        if (!validateParameter(serviceParameter)) {
            throw new ParameterException(PARAMETER_SERVICE_ID);
        }

        long id;
        try {
            id = Long.parseLong(serviceParameter);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParameterException(PARAMETER_SERVICE_ID);
        }

        Dao<Tariff, Long> tariffDao = tariffDaoFactory.getDao();

        List<Tariff> tariffs = tariffDao.customReadMany("READ MANY serviceId", id);
        LOG.trace("Found in DB: tariffs --> " + tariffs);

        String sortType = getParameter(PARAMETER_SORT, request);
        LOG.trace("Request parameter: " + PARAMETER_SORT + " --> " + sortType);

        if (!validateParameter(sortType)) {
            sortType = SORT_TYPE_NAME_ASC;
        }

        Comparator<? super Tariff> comparator;
        switch (sortType) {
            case SORT_TYPE_MONTH_PAYMENT_ASC:
                comparator = Comparator.comparing(Tariff::getMonthPayment);
                break;
            case SORT_TYPE_CONNECTION_PAYMENT_ASC:
                comparator = Comparator.comparing(Tariff::getConnectionPayment);
                break;
            case SORT_TYPE_NAME_DESC:
                comparator = Comparator.comparing(Tariff::getName).reversed();
                break;
            case SORT_TYPE_MONTH_PAYMENT_DESC:
                comparator = Comparator.comparing(Tariff::getMonthPayment).reversed();
                break;
            case SORT_TYPE_CONNECTION_PAYMENT_DESC:
                comparator = Comparator.comparing(Tariff::getConnectionPayment).reversed();
                break;
            default:
                comparator = Comparator.comparing(Tariff::getName);
                break;
        }
        tariffs.sort(comparator);

        Dao<Service, Long> serviceDao = serviceDaoFactory.getDao();
        Service linkedService = serviceDao.read(id);
        LOG.trace("Found in DB: service --> " + linkedService);

        setAttribute(ATTRIBUTE_REQUESTED_TARIFFS, tariffs, request);
        setAttribute(ATTRIBUTE_REQUESTED_TARIFFS_LINKED_SERVICE, linkedService, request);
        LOG.trace(String.format("Set the request attribute: %s --> %s"
                , ATTRIBUTE_REQUESTED_TARIFFS, tariffs));
        LOG.trace(String.format("Set the request attribute: %s --> %s"
                , ATTRIBUTE_REQUESTED_TARIFFS_LINKED_SERVICE, linkedService));

        LOG.debug("Command finished");
        return Path.MOVE_BY_FORWARD_TO_TARIFF_LIST;
    }
}
