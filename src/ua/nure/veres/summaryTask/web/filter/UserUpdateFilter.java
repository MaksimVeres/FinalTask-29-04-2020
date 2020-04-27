package ua.nure.veres.summaryTask.web.filter;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.ee.security.SecurityManager;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * User update filter, calls SecurityManager refreshUser method to synchronize user session User-object
 * and server linked User-object.
 */
public class UserUpdateFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(UserUpdateFilter.class);

    private static final String LOG_FILTER_FINISHED = "Filter finished";

    private static final String ATTRIBUTE_USER = "user";

    static {
        BasicConfigurator.configure();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.debug("Filter initialization starts");
        //no op
        LOG.debug("Filter initialization finished");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse
            , FilterChain filterChain) throws IOException, ServletException {

        LOG.debug("Filter starts");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute(ATTRIBUTE_USER);
        LOG.trace(String.format("Session attribute: %s --> %s", ATTRIBUTE_USER, user));

        if (user == null) {
            LOG.debug(LOG_FILTER_FINISHED);
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            if (SecurityManager.getInstance().refreshUser(request)) {
                LOG.debug(LOG_FILTER_FINISHED);
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                LOG.debug(LOG_FILTER_FINISHED);
                request.getRequestDispatcher(Path.COMMAND_LOGOUT).forward(request, servletResponse);
            }
        }
    }

    @Override
    public void destroy() {
        LOG.debug("Filter destruction starts");
        // no op
        LOG.debug("Filter destruction finished");
    }
}
