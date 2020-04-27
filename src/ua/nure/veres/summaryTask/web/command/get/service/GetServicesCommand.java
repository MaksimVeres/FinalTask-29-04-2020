package ua.nure.veres.summaryTask.web.command.get.service;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.AttributeException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;
import ua.nure.veres.summaryTask.web.command.get.GetCommand;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Get services command.
 */
public class GetServicesCommand extends GetCommand {

    private static final long serialVersionUID = 5646778888899000234L;

    private static final String PARAMETER_SORT_COLUMN = "sortColumn";

    private static final String PARAMETER_SEARCH_ALL = "searchAll";

    private static final String PARAMETER_SEARCH_NAME = "searchName";

    private static final String ATTRIBUTE_SERVICES = "services";

    private static final String ATTRIBUTE_REPLY_REQUEST = "replyRequest";

    private static final String ATTRIBUTE_REQUESTED_SERVICES = "requestedServices";

    private static final String SORT_COLUMN_ID = "id";

    private static final String SORT_COLUMN_NAME = "name";

    private static final String VALUE_TRUE = "true";

    private static final String COMMAND_GET_SERVICES = "process?command=getServices";

    private static final String COMMAND_GET_SERVICES_SEARCH_ALL
            = COMMAND_GET_SERVICES + "&" + PARAMETER_SEARCH_ALL + "=" + VALUE_TRUE;

    private static final String COMMAND_GET_SERVICES_SEARCH_NAME
            = COMMAND_GET_SERVICES + "&" + PARAMETER_SEARCH_NAME + "=";

    private static final Logger LOG = Logger.getLogger(GetServicesCommand.class);

    private static final String LOG_REQUEST_PARAMETER = "Request parameter: %s --> %s";

    static {
        BasicConfigurator.configure();
    }

    @Override
    protected String doGetCommand(HttpServletRequest request, HttpServletResponse response) throws AppException {
        HttpSession session = request.getSession();
        checkUserAccess(session);
        return getResult(request);
    }

    /**
     * Gets a list of services from a database and sorts it.
     *
     * @param request HttpServletRequest
     * @return replyRequest to command
     */
    @SuppressWarnings("unchecked")
    private String getResult(HttpServletRequest request) throws ParameterException {
        String searchAll = getParameter(PARAMETER_SEARCH_ALL, request);
        LOG.trace(String.format(LOG_REQUEST_PARAMETER, PARAMETER_SEARCH_ALL, searchAll));

        String replyRequest;

        ServletContext context = request.getServletContext();
        ArrayList<Service> services = new ArrayList<>();
        Object servicesContextAttribute = context.getAttribute(ATTRIBUTE_SERVICES);
        if (servicesContextAttribute instanceof ArrayList) {
            services = new ArrayList<>((List<Service>) servicesContextAttribute);
        }

        if (VALUE_TRUE.equals(searchAll)) {
            replyRequest = COMMAND_GET_SERVICES_SEARCH_ALL;
        } else {
            String name = getParameter(PARAMETER_SEARCH_NAME, request);
            LOG.trace(String.format(LOG_REQUEST_PARAMETER, PARAMETER_SEARCH_NAME, name));

            if (!validateParameter(name)) {
                throw new ParameterException(PARAMETER_SEARCH_NAME);
            }

            services.removeIf((Service s) -> !s.getName().contains(name));
            replyRequest = COMMAND_GET_SERVICES_SEARCH_NAME + name;
        }

        sortServices(services, request);

        HttpSession session = request.getSession();
        setAttribute(ATTRIBUTE_REQUESTED_SERVICES, services, session);
        setAttribute(ATTRIBUTE_REPLY_REQUEST, replyRequest, session);
        LOG.trace(String.format("Set the session attribute: %s --> %s"
                , ATTRIBUTE_REQUESTED_SERVICES, services));
        LOG.trace(String.format("Set the session attribute: %s --> %s"
                , ATTRIBUTE_REPLY_REQUEST, replyRequest));

        return Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SERVICE_LIST;
    }

    /**
     * Sorts services by sortColumn request parameter.
     *
     * @param services Services list
     * @param request  HttpServletRequest
     */
    private void sortServices(List<Service> services, HttpServletRequest request) {
        String sortColumn = getParameter(PARAMETER_SORT_COLUMN, request);
        LOG.trace(String.format(LOG_REQUEST_PARAMETER, PARAMETER_SORT_COLUMN, sortColumn));

        if (sortColumn == null) {
            sortColumn = SORT_COLUMN_ID;
        }

        Comparator<? super Service> comparator;
        if (SORT_COLUMN_NAME.equals(sortColumn)) {
            comparator = Comparator.comparing(Service::getName);
        } else {
            comparator = Comparator.comparing(Service::getId);
        }
        services.sort(comparator);
    }


}
