package ua.nure.veres.summaryTask.ee.data;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.ee.data.service.ServiceSynchronizerWorker;
import ua.nure.veres.summaryTask.exception.AppException;

import javax.servlet.ServletContext;
import java.util.Comparator;
import java.util.List;

/**
 * DataSynchronizer class to synchronize data on server with data on database.
 */
public class DataSynchronizer {

    private static final long SERVICES_UPDATE_FREQUENCY_MS = 60000;

    private static ServletContext servicesContext;

    private static final Logger LOG = Logger.getLogger(DataSynchronizer.class);

    private static final String ATTRIBUTE_SERVICES = "services";

    private static DataSynchronizer instance;

    static {
        BasicConfigurator.configure();
    }

    private DataSynchronizer() {
        //no op
    }

    /**
     * Starts thread to synchronize services context attribute.
     *
     * @param context ServletContext
     */
    public void startSyncServices(ServletContext context)  {
        servicesContext = context;
        Thread servicesSynchronizer = new ServiceSynchronizerWorker(context, SERVICES_UPDATE_FREQUENCY_MS);
        servicesSynchronizer.start();
    }

    /**
     * Does operation of services context attribute synchronization.
     *
     * @param context ServletContext
     */
    public void synchronizeServices(ServletContext context) throws AppException {
        DaoFactory daoFactory = new ServiceDaoFactory();
        Dao<Service, Long> serviceDao = daoFactory.getDao();

        List<Service> services = serviceDao.read();
        LOG.trace("Found in DB: size of founded services --> " + services.size());
        services.sort(Comparator.comparing(Service::getName));
        context.setAttribute(ATTRIBUTE_SERVICES, services);

    }

    /**
     * Does operation of services context attribute synchronization.
     */
    public void synchronizeServices() throws AppException {
        synchronizeServices(servicesContext);
    }

    public static synchronized DataSynchronizer getInstance() {
        if (instance == null) {
            instance = new DataSynchronizer();
        }

        return instance;
    }

}
