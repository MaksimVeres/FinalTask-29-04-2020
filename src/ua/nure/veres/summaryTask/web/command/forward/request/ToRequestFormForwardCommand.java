package ua.nure.veres.summaryTask.web.command.forward.request;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.command.forward.ForwardCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Abstract to request form command.
 */
abstract class ToRequestFormForwardCommand extends ForwardCommand {
    static final String PARAMETER_USER_SERVICE_ID = "userServiceId";

    static final String PARAMETER_TARIFF_ID = "tariffId";

    private static final String ATTRIBUTE_USER_REQUEST_TARIFF = "userRequestTariff";

    private static final String ATTRIBUTE_USER_REQUEST_SERVICE = "userRequestService";

    private static final Logger LOG = Logger.getLogger(ToRequestFormForwardCommand.class);

    static {
        BasicConfigurator.configure();
    }

    /**
     * Prepares request moving logic.
     *
     * @param tariffIdParameter Id parameter of tariff
     */
    void prepareRequest(String tariffIdParameter, HttpServletRequest request) throws AppException {
        long id = Long.parseLong(tariffIdParameter);

        DaoFactory daoFactory = new TariffDaoFactory();
        Dao<Tariff, Long> tariffDao = daoFactory.getDao();

        Tariff userRequestTariff = tariffDao.read(id);

        LOG.trace("Found in DB: tariff --> " + userRequestTariff);

        if (userRequestTariff == null) {
            throw new ParameterException(PARAMETER_TARIFF_ID);
        }

        daoFactory = new ServiceDaoFactory();
        Dao<Service, Long> serviceDao = daoFactory.getDao();

        Service userRequestService = serviceDao.read(
                userRequestTariff.getServiceId()
        );

        LOG.trace("Found in DB: service --> " + userRequestService);

        if (userRequestService == null) {
            throw new ParameterException(PARAMETER_TARIFF_ID);
        }

        HttpSession session = request.getSession();
        setAttribute(ATTRIBUTE_USER_REQUEST_TARIFF, userRequestTariff, session);
        setAttribute(ATTRIBUTE_USER_REQUEST_SERVICE, userRequestService, session);

        LOG.info("Set session attribute " + ATTRIBUTE_USER_REQUEST_TARIFF + " --> " + userRequestTariff);
        LOG.info("Set session attribute " + ATTRIBUTE_USER_REQUEST_SERVICE + " --> " + userRequestService);
    }
}
