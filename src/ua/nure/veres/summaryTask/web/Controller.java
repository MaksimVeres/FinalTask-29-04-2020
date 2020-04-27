package ua.nure.veres.summaryTask.web;

import ua.nure.veres.summaryTask.service.ServiceManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller.
 */
public class Controller extends HttpServlet {

    private static final long serialVersionUID = 2423353715955164816L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.doService(request, response);
    }
}
