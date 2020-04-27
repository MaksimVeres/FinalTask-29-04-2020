package ua.nure.veres.summaryTask.service;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.ExceptionCatcher;
import ua.nure.veres.summaryTask.web.Commands;
import ua.nure.veres.summaryTask.web.command.Command;
import ua.nure.veres.summaryTask.web.command.CommandContainer;
import ua.nure.veres.summaryTask.web.command.forward.ForwardCommand;
import ua.nure.veres.summaryTask.web.command.get.GetCommand;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * App manager of services execution.
 */
public final class ServiceManager {

    private static final String PARAMETER_COMMAND = "command";

    private static final Logger LOG = Logger.getLogger(ServiceManager.class);

    private static ServiceManager instance;

    static {
        BasicConfigurator.configure();
    }

    private ServiceManager() {
        //no op
    }

    /**
     * Command execution switcher.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    public void doService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOG.debug("Start do service");

        String commandName = request.getParameter(PARAMETER_COMMAND);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_COMMAND, commandName));

        Command command = CommandContainer.get(commandName);
        String simpleCommandName = command.toString();

        LOG.debug("Try to execute command");
        try {
            switch (simpleCommandName) {

                //Redirect commands
                case Commands.LOGIN_COMMAND:
                case Commands.LOGOUT_COMMAND:
                case Commands.CHANGE_FULL_NAME_COMMAND:
                case Commands.CHANGE_PASSWORD_COMMAND:
                case Commands.CHANGE_SERVICE_COMMAND:
                case Commands.CHANGE_TARIFF_COMMAND:
                case Commands.CHANGE_USER_STATUS_COMMAND:
                case Commands.CHANGE_USER_ROLE_COMMAND:
                case Commands.ORDER_SERVICE_COMMAND:
                case Commands.DESCRIBE_SERVICE_COMMAND:
                case Commands.SUBMIT_SUBSCRIBE_REQUEST_COMMAND:
                case Commands.SUBMIT_DESCRIBE_REQUEST_COMMAND:
                case Commands.DECLINE_SUBSCRIBE_REQUEST_COMMAND:
                case Commands.DECLINE_DESCRIBE_REQUEST_COMMAND:
                case Commands.DELETE_SERVICE_COMMAND:
                case Commands.DELETE_TARIFF_COMMAND:
                case Commands.DELETE_USER_COMMAND:
                case Commands.CREATE_SERVICE_COMMAND:
                case Commands.CREATE_TARIFF_COMMAND:
                case Commands.LANGUAGE_SWITCH_COMMAND:
                case Commands.REPLENISH_USER_ACCOUNT_COMMAND:
                    doCommandByRedirect(request, response, command);
                    break;

                //Forward commands
                case Commands.SUBMIT_REGISTER_COMMAND:
                case Commands.UPDATE_USER_SERVICES_COMMAND:
                case Commands.OPEN_TARIFFS_COMMAND:
                case Commands.NO_COMMAND:
                    doCommandByForward(request, response, command);
                    break;

                //Get commands
                case Commands.GET_USER_SUBSCRIBE_REQUESTS_COMMAND:
                case Commands.GET_USER_DESCRIBE_REQUESTS_COMMAND:
                case Commands.GET_SERVICES_COMMAND:
                case Commands.GET_TARIFFS_COMMAND:
                case Commands.GET_TARIFFS_TXT_COMMAND:
                case Commands.GET_USERS_COMMAND:
                    doGetCommand(request, response, command);
                    break;

                //Move commands
                case Commands.TO_MAIN_PAGE_COMMAND:
                case Commands.TO_LOGIN_PAGE_COMMAND:
                case Commands.TO_PERSONAL_CABINET_COMMAND:
                case Commands.TO_PERSONAL_CABINET_SERVICES_COMMAND:
                case Commands.TO_ORDER_FORM_COMMAND:
                case Commands.TO_DESCRIBE_FORM_COMMAND:
                case Commands.TO_TARIFF_CHANGE_FORM_COMMAND:
                    doMoveToPageCommand(request, response, command);
                    break;

                default:
                    break;
            }
        } catch (AppException ex) {
            LOG.error(ex.getMessage(), ex);
            ExceptionCatcher exceptionCatcher = ExceptionCatcher.getInstance();
            exceptionCatcher.handle(ex, request, response);
        } finally {
            LOG.debug("End do service");
        }
    }

    /**
     * Does command by forward method.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param command  Command to execute
     */
    private void doCommandByForward(HttpServletRequest request, HttpServletResponse response, Command command)
            throws AppException, IOException, ServletException {
        String forward = command.execute(request, response);
        request.getRequestDispatcher(forward).forward(request, response);
    }

    /**
     * Does command by redirect method.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param command  Command to execute
     */
    private void doCommandByRedirect(HttpServletRequest request, HttpServletResponse response, Command command)
            throws AppException, IOException, ServletException {
        String redirect = command.execute(request, response);
        response.sendRedirect(redirect);
    }

    /**
     * Does get command method.
     *
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param getCommand Command to execute
     */
    private void doGetCommand(HttpServletRequest request, HttpServletResponse response
            , Command getCommand) throws IOException, AppException {
        String getResult = ((GetCommand) getCommand).execute(request, response);
        if (GetCommand.COMMAND_EXECUTION_SUCCESS.equals(getResult)) {
            return;
        }
        response.sendRedirect(getResult);
    }

    /**
     * Does move to page command.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param command  Command to execute
     */
    private void doMoveToPageCommand(HttpServletRequest request, HttpServletResponse response
            , Command command) throws IOException, ServletException, AppException {
        moveTo((ForwardCommand) command, request, response);
    }

    /**
     * Moves to page by forward method.
     *
     * @param forwardCommand Forward command to execute
     * @param request        HttpServletRequest
     * @param response       HttpServletResponse
     */
    private void moveTo(ForwardCommand forwardCommand, HttpServletRequest request
            , HttpServletResponse response) throws ServletException, IOException, AppException {
        String forward = forwardCommand.execute(request, response);
        String forwardMethod = forwardCommand.getForwardMethod(request);
        if (ForwardCommand.FORWARD_METHOD_REDIRECT.equals(forwardMethod) || forwardMethod == null) {
            response.sendRedirect(forward);
        } else if (ForwardCommand.FORWARD_METHOD_FORWARD.equals(forwardMethod)) {
            request.getRequestDispatcher(forward).forward(request, response);
        }
    }

    public static synchronized ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }
}
