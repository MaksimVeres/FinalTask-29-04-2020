package ua.nure.veres.summaryTask.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import ua.nure.veres.summaryTask.ee.data.DataSynchronizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Context listener.
 */
public class ContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(ContextListener.class);

    static {
        BasicConfigurator.configure();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        log("Servlet context destruction starts");
        // no op
        log("Servlet context destruction finished");
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        log("Servlet context initialization starts");

        ServletContext servletContext = event.getServletContext();
        initLog4J(servletContext);
        initCommandContainer();
        initServicesList(servletContext);
        initLocales(servletContext);

        log("Servlet context initialization finished");
    }

    /**
     * Initializes log4j framework.
     *
     * @param servletContext servlet context to init
     */
    private void initLog4J(ServletContext servletContext) {
        log("Log4J initialization started");
        try {
            PropertyConfigurator.configure(
                    servletContext.getRealPath("WEB-INF/log4j.properties"));
            LOG.debug("Log4j has been initialized");
        } catch (Exception ex) {
            log("Cannot configure Log4j");
            LOG.error(ex.getMessage(), ex);
        }
        log("Log4J initialization finished");
    }

    /**
     * Initializes CommandContainer.
     */
    private void initCommandContainer() {

        // initialize commands container
        // just load class to JVM
        try {
            Class.forName("ua.nure.veres.summaryTask.web.command.CommandContainer");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Cannot initialize Command Container");
        }
    }

    /**
     * Initializes services list context attribute.
     *
     * @param context servlet context
     */
    private void initServicesList(ServletContext context) {
        DataSynchronizer dataSynchronizer = DataSynchronizer.getInstance();
        dataSynchronizer.startSyncServices(context);
    }

    /**
     * Initializes server locales.
     */
    private void initLocales(ServletContext servletContext) {
        // obtain file name with locales descriptions
        String localesFileName = servletContext.getInitParameter("locales");

        // obtain real path on server
        String localesFileRealPath = servletContext.getRealPath(localesFileName);

        // locale descriptions
        Properties locales = new Properties();
        try {
            locales.load(new FileInputStream(localesFileRealPath));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        // save descriptions to servlet context
        servletContext.setAttribute("locales", locales);
        LOG.trace("Loaded locales --> " + locales);
    }

    private void log(String msg) {
        LOG.trace("[ContextListener] " + msg);
    }
}