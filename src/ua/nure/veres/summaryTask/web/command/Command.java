package ua.nure.veres.summaryTask.web.command;

import ua.nure.veres.summaryTask.db.Role;
import ua.nure.veres.summaryTask.db.Status;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;

/**
 * Main interface for the Command pattern implementation.
 */
public abstract class Command implements Serializable {

    /**
     * Validates params to be not null.
     *
     * @param values params to validate
     */
    private void validateNotNull(Object... values) {
        if (values == null) {
            throw new IllegalArgumentException();
        }
        for (Object value : values) {
            if (value == null) {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * Gets an attribute from ServletContext by it's name.
     *
     * @param name    name of attribute
     * @param context context whose attribute will be extracted
     */
    protected Object getAttribute(String name, ServletContext context) {
        validateNotNull(name, context);
        return context.getAttribute(name);
    }

    /**
     * Gets an attribute from HttpSession by it's name.
     *
     * @param name    name of attribute
     * @param session session whose attribute will be extracted
     */
    protected Object getAttribute(String name, HttpSession session) {
        validateNotNull(name, session);
        return session.getAttribute(name);
    }

    /**
     * Gets an attribute from HttpServletRequest by it's name.
     *
     * @param name    name of attribute
     * @param request request whose attribute will be extracted
     */
    protected Object getAttribute(String name, HttpServletRequest request) {
        validateNotNull(name, request);
        return request.getAttribute(name);
    }

    /**
     * Sets an attribute value to HttpServletRequest by it's name.
     *
     * @param name    name of attribute
     * @param value   value of attribute
     * @param request request whose attribute will be changed
     */
    protected void setAttribute(String name, Object value, HttpServletRequest request) {
        validateNotNull(name, value, request);
        request.setAttribute(name, value);
    }

    /**
     * Sets an attribute value to HttpSession by it's name.
     *
     * @param name    name of attribute
     * @param value   serializable value of attribute
     * @param session session whose attribute will be changed
     */
    protected void setAttribute(String name, Serializable value, HttpSession session) {
        validateNotNull(name, value, session);
        session.setAttribute(name, value);
    }

    /**
     * Sets an attribute value to ServletContext by it's name.
     *
     * @param name    name of attribute
     * @param value   value of attribute
     * @param context context whose attribute will be changed
     */
    protected void setAttribute(String name, Object value, ServletContext context) {
        validateNotNull(name, value, context);
        context.setAttribute(name, value);
    }

    /**
     * Removes an attribute of request.
     *
     * @param name    attribute name
     * @param request request whose attribute will be removed
     */
    protected void removeAttribute(String name, HttpServletRequest request) {
        validateNotNull(name);
        request.removeAttribute(name);
    }

    /**
     * Removes an attribute of session.
     *
     * @param name    attribute name
     * @param session session whose attribute will be removed
     */
    protected void removeAttribute(String name, HttpSession session) {
        validateNotNull(name);
        session.removeAttribute(name);
    }

    /**
     * Removes an attribute of context.
     *
     * @param name    attribute name
     * @param context context whose attribute will be removed
     */
    protected void removeAttribute(String name, ServletContext context) {
        validateNotNull(name);
        context.removeAttribute(name);
    }

    /**
     * Gets a parameter from HttpServletRequest it's by name.
     *
     * @param name    name of parameter
     * @param request request whose parameter will be extracted
     */
    protected String getParameter(String name, HttpServletRequest request) {
        validateNotNull(name, request);
        return request.getParameter(name);
    }

    /**
     * Checks an user to have access access.
     *
     * @param user       user to check
     * @param targetRole access that user should have
     */
    protected boolean checkAccess(User user, Role targetRole) {
        validateNotNull(user, targetRole);
        return Role.getRole(user) == targetRole;
    }

    /**
     * Checks an user to have target status.
     *
     * @param user         user to check
     * @param targetStatus status that user should have
     */
    protected boolean checkStatus(User user, Status targetStatus) {
        validateNotNull(user, targetStatus);
        return Status.getStatus(user) == targetStatus;
    }

    /**
     * Validates parameter to be not empty and not null.
     *
     * @param parameter parameter to validate
     */
    public boolean validateParameter(String parameter) {
        return parameter != null && !parameter.isEmpty();
    }

    /**
     * Execution method for command.
     *
     * @return Address to go once the command is executed.
     */
    public abstract String execute(HttpServletRequest request,
                                   HttpServletResponse response) throws IOException, ServletException,
            AppException;

    @Override
    public final String toString() {
        return getClass().getSimpleName();
    }
}
