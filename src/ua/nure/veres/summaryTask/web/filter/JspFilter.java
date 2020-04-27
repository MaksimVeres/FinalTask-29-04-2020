package ua.nure.veres.summaryTask.web.filter;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Jsp access filter.
 */
public class JspFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(JspFilter.class);

    private static final String EMPTY_PATH = "/";

    private static final String FORMAT_JSP = ".jsp";

    private static final String COMMAND_NO_COMMAND = "process?command=noCommand";

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
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        LOG.debug("Filter starts");

        String path = ((HttpServletRequest) servletRequest).getRequestURI();
        if (EMPTY_PATH.equals(path) || !path.contains(FORMAT_JSP)) {
            LOG.debug("Filter finished");
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            LOG.debug("Filter finished");
            servletRequest.getRequestDispatcher(COMMAND_NO_COMMAND).forward(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        LOG.debug("Filter destruction starts");
        // no op
        LOG.debug("Filter destruction finished");
    }
}
