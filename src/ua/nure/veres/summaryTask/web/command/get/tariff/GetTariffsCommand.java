package ua.nure.veres.summaryTask.web.command.get.tariff;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.get.GetCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Get tariff command.
 */
public class GetTariffsCommand extends GetCommand {

    private static final long serialVersionUID = 555324443267654678L;

    private static final String PARAMETER_SORT_COLUMN = "sortColumn";

    private static final String PARAMETER_SEARCH_ALL = "searchAll";

    private static final String PARAMETER_SEARCH_NAME = "searchName";

    private static final String ATTRIBUTE_REQUESTED_TARIFFS = "requestedTariffs";

    private static final String ATTRIBUTE_REPLY_REQUEST = "replyRequest";

    private static final String SORT_COLUMN_ID = "id";

    private static final String SORT_COLUMN_NAME = "name";

    private static final String SORT_COLUMN_CONNECTION_PAYMENT = "connectionPayment";

    private static final String SORT_COLUMN_MONTH_PAYMENT = "monthPayment";

    private static final String SORT_COLUMN_FEATURE = "feature";

    private static final String SORT_COLUMN_SERVICE_ID = "serviceId";

    private static final String VALUE_TRUE = "true";

    private static final String COMMAND_GET_TARIFFS = "process?command=getTariffs";

    private static final String COMMAND_GET_TARIFFS_SEARCH_ALL
            = COMMAND_GET_TARIFFS + "&" + PARAMETER_SEARCH_ALL + "=" + VALUE_TRUE;

    private static final String COMMAND_GET_TARIFFS_SEARCH_NAME
            = COMMAND_GET_TARIFFS + "&" + PARAMETER_SEARCH_NAME + "=";

    private static final Logger LOG = Logger.getLogger(GetTariffsCommand.class);

    private static final String LOG_REQUEST_PARAMETER = "Request parameter: %s --> %s";

    private transient DaoFactory tariffDaoFactory;

    static {
        BasicConfigurator.configure();
    }

    public GetTariffsCommand(DaoFactory tariffDaoFactory) {
        this.tariffDaoFactory = tariffDaoFactory;
    }

    @Override
    protected String doGetCommand(HttpServletRequest request, HttpServletResponse response) throws AppException {
        HttpSession session = request.getSession();
        checkUserAccess(session);
        String replyRequest;

        String searchAll = getParameter(PARAMETER_SEARCH_ALL, request);
        LOG.trace(String.format(LOG_REQUEST_PARAMETER, PARAMETER_SEARCH_ALL, searchAll));

        Dao<Tariff, Long> tariffDao = tariffDaoFactory.getDao();
        List<Tariff> dbTariffs = tariffDao.read();
        ArrayList<Tariff> tariffs = new ArrayList<>();
        if (dbTariffs instanceof ArrayList) {
            tariffs = (ArrayList<Tariff>) dbTariffs;
        }
        LOG.trace("Found in DB: tariffs, size --> " + tariffs.size());

        if (VALUE_TRUE.equals(searchAll)) {
            replyRequest = COMMAND_GET_TARIFFS_SEARCH_ALL;
        } else {
            String name = getParameter(PARAMETER_SEARCH_NAME, request);
            LOG.trace(String.format(LOG_REQUEST_PARAMETER, PARAMETER_SEARCH_NAME, name));

            if (!validateParameter(name)) {
                throw new ParameterException(PARAMETER_SEARCH_NAME);
            }

            tariffs.removeIf((Tariff t) -> !t.getName().contains(name));
            replyRequest = COMMAND_GET_TARIFFS_SEARCH_NAME + name;
        }
        sortTariffs(tariffs, request);

        setAttribute(ATTRIBUTE_REQUESTED_TARIFFS, tariffs, session);
        setAttribute(ATTRIBUTE_REPLY_REQUEST, replyRequest, session);
        LOG.trace(String.format("Set the session attribute: %s, size --> %s"
                , ATTRIBUTE_REQUESTED_TARIFFS, tariffs.size()));
        LOG.trace(String.format("Set the session attribute: %s --> %s"
                , ATTRIBUTE_REPLY_REQUEST, replyRequest));

        return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_TARIFF_LIST;
    }

    /**
     * Sorts a tariffs by a sortColumn request parameter.
     *
     * @param tariffs Tariffs list
     * @param request HttpServletRequest
     */
    private void sortTariffs(List<Tariff> tariffs, HttpServletRequest request) {
        String sortColumn = getParameter(PARAMETER_SORT_COLUMN, request);
        LOG.trace(String.format(LOG_REQUEST_PARAMETER, PARAMETER_SORT_COLUMN, sortColumn));

        if (sortColumn == null) {
            sortColumn = SORT_COLUMN_ID;
        }

        Comparator<? super Tariff> comparator;
        switch (sortColumn) {
            case SORT_COLUMN_NAME:
                comparator = Comparator.comparing(Tariff::getName);
                break;
            case SORT_COLUMN_CONNECTION_PAYMENT:
                comparator = Comparator.comparing(Tariff::getConnectionPayment);
                break;
            case SORT_COLUMN_MONTH_PAYMENT:
                comparator = Comparator.comparing(Tariff::getMonthPayment);
                break;
            case SORT_COLUMN_FEATURE:
                comparator = Comparator.comparing(Tariff::getFeature);
                break;
            case SORT_COLUMN_SERVICE_ID:
                comparator = Comparator.comparing(Tariff::getServiceId);
                break;
            default:
                comparator = Comparator.comparing(Tariff::getId);
                break;
        }
        tariffs.sort(comparator);
    }
}
