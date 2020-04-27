package ua.nure.veres.summaryTask.web.filter;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MoveToFilter implements Filter {
    private FilterConfig filterConfig;

    private static final String MOVE_TO_REDIRECT_SIGNATURE = "/page";

    private static final String MOVE_TO_FORWARD_SIGNATURE = "/forward";

    private static final String MOVE_TO_REDIRECT_PAGE_NAME_PARAMETER = "name";

    private static final String MOVE_TO_FORWARD_PAGE_NAME_PARAMETER = "page";

    private static final Logger LOG = Logger.getLogger(MoveToFilter.class);

    static {
        BasicConfigurator.configure();
    }

    @Override
    public void init(FilterConfig filterConfig) {
        LOG.debug("Filter initialization starts");
        this.filterConfig = filterConfig;
        LOG.debug("Filter initialization finished");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse
            , FilterChain filterChain) throws IOException, ServletException {
        LOG.debug("Filter starts");
        String uri = ((HttpServletRequest) servletRequest).getRequestURI();
        String query = ((HttpServletRequest) servletRequest).getQueryString();
        if (MOVE_TO_REDIRECT_SIGNATURE.equals(uri) || MOVE_TO_FORWARD_SIGNATURE.equals(uri)) {

            if (query == null) {
                ((HttpServletResponse) servletResponse).sendRedirect(Path.MOVE_BY_REDIRECT_TO_MAIN_PAGE);
                LOG.debug("Filter finished");
                return;
            }

            String pageParameter = servletRequest.getParameter(MOVE_TO_FORWARD_PAGE_NAME_PARAMETER);
            if (pageParameter != null) {

                String forward = filterConfig.getInitParameter(pageParameter);
                if (forward != null) {
                    servletRequest.getRequestDispatcher(forward).forward(servletRequest, servletResponse);
                    LOG.debug("Filter finished");
                    return;
                }

                servletRequest.getRequestDispatcher(Path.MOVE_BY_FORWARD_TO_MAIN_PAGE)
                        .forward(servletRequest, servletResponse);
                LOG.debug("Filter finished");
                return;
            }

            String queryString = ((HttpServletRequest) servletRequest).getQueryString();
            if (queryString.length() > 4) {
                String nameParameter = getPageName(queryString);
                String forward = filterConfig.getInitParameter(nameParameter);
                if (forward != null) {
                    servletRequest.getRequestDispatcher(forward).forward(servletRequest, servletResponse);
                } else {
                    servletRequest.getRequestDispatcher(Path.MOVE_BY_FORWARD_TO_MAIN_PAGE)
                            .forward(servletRequest, servletResponse);
                }

                LOG.debug("Filter finished");
                return;
            }

            servletRequest.getRequestDispatcher(Path.MOVE_BY_FORWARD_TO_MAIN_PAGE)
                    .forward(servletRequest, servletResponse);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        LOG.debug("Filter destruction starts");
        // no op
        LOG.debug("Filter destruction finished");
    }

    /**
     * Gets name of target page from request query string.
     *
     * @param queryString query string of the request.
     * @return String contains name of target page.
     */
    private String getPageName(String queryString) {
        int nameParameterIndex = queryString.indexOf(MOVE_TO_REDIRECT_PAGE_NAME_PARAMETER);
        return queryString.substring(nameParameterIndex + MOVE_TO_REDIRECT_PAGE_NAME_PARAMETER.length() + 1);
    }
}
