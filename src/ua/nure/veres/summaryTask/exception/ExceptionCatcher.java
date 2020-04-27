package ua.nure.veres.summaryTask.exception;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Exception catcher class,
 * converts technical exception messages to user-friendly.
 */
public class ExceptionCatcher {

    private static final String ATTRIBUTE_ERROR_TITLE = "errorTitle";

    private static final String ATTRIBUTE_UF_ERROR_MESSAGE = "ufErrorMessage";

    private static final String ATTRIBUTE_ERROR_MESSAGE = "errorMessage";

    private static final Logger LOG = Logger.getLogger(ExceptionCatcher.class);

    private static final String LOG_SET_REQUEST_ATTRIBUTE = "Set the request attribute: %s --> %s";

    private static ExceptionCatcher instance;

    static {
        BasicConfigurator.configure();
    }

    private ExceptionCatcher() {
        //no op
    }

    /**
     * Handles exception and converts to request.
     * @param exception AppException to handle
     * @param request HttpServletRequest
     */
    public void handle(AppException exception, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOG.debug("Handling exception --> " + exception);

        Class exceptionClass = exception.getClass();
        String title = UFMessages.getUFTitle(exceptionClass);
        String ufMessage = UFMessages.getUFMessage(exceptionClass);
        String message = exception.getMessage();
        request.setAttribute(ATTRIBUTE_ERROR_TITLE, title);
        request.setAttribute(ATTRIBUTE_UF_ERROR_MESSAGE, ufMessage);
        request.setAttribute(ATTRIBUTE_ERROR_MESSAGE, message);
        LOG.trace(String.format(LOG_SET_REQUEST_ATTRIBUTE
                , ATTRIBUTE_ERROR_TITLE, title));
        LOG.trace(String.format(LOG_SET_REQUEST_ATTRIBUTE
                , ATTRIBUTE_UF_ERROR_MESSAGE, ufMessage));
        LOG.trace(String.format(LOG_SET_REQUEST_ATTRIBUTE
                , ATTRIBUTE_ERROR_MESSAGE, message));

        request.getRequestDispatcher(Path.PAGE_ERROR).forward(request, response);
    }

    public static synchronized ExceptionCatcher getInstance() {
        if (instance == null) {
            instance = new ExceptionCatcher();
        }
        return instance;
    }

}
