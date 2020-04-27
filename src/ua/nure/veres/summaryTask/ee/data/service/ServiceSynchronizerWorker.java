package ua.nure.veres.summaryTask.ee.data.service;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.ee.data.DataSynchronizer;
import ua.nure.veres.summaryTask.exception.AppException;

import javax.servlet.ServletContext;

/**
 * Calls with chosen period DataSynchronizer#updateServices method.
 */
public class ServiceSynchronizerWorker extends Thread {

    private static final Logger LOG = Logger.getLogger(ServiceSynchronizerWorker.class);

    private ServletContext context;
    private long synchronizeFrequency;

    static {
        BasicConfigurator.configure();
    }

    public ServiceSynchronizerWorker(ServletContext context, long synchronizeFrequency) {
        this.context = context;
        this.synchronizeFrequency = synchronizeFrequency;
    }

    @Override
    public void run() {
        DataSynchronizer dataSynchronizer = DataSynchronizer.getInstance();
        while (true) {
            LOG.debug("Start synchronizing list of services");

            try {
                dataSynchronizer.synchronizeServices(context);
            } catch (AppException ex) {
                LOG.error(ex.getMessage(), ex);
                LOG.error("Cannot continue synchronizing services, stopping worker");
                interrupt();
                return;
            }
            LOG.debug("End synchronizing list of services");

            try {
                sleep(synchronizeFrequency);
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage(), ex);
                interrupt();
                return;
            }
        }
    }
}
