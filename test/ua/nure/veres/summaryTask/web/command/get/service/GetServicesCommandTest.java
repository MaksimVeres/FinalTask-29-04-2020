package ua.nure.veres.summaryTask.web.command.get.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GetServicesCommandTest {

    @InjectMocks
    private GetServicesCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServletContext servletContext;

    @Mock
    private List<Service> services;

    @Mock
    private User user;

    @Before
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(request.getServletContext()).thenReturn(servletContext);
    }

    @Test
    public void doGetCommandAuthorizedAdminWithRightParametersSearchAll() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);
        doNothing().when(session).setAttribute(anyString(), any());

        when(user.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("true");

        when(servletContext.getAttribute("services")).thenReturn(services);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SERVICE_LIST;
        String actually = object.doGetCommand(request, response);
        assertEquals(expected, actually);

        verify(session, times(2)).setAttribute(anyString(), any());
    }

    @Test
    public void doGetCommandAuthorizedAdminWithRightParametersSearchOne() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);
        doNothing().when(session).setAttribute(anyString(), any());

        when(user.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("false");
        when(request.getParameter("searchName")).thenReturn("testName");

        when(servletContext.getAttribute("services")).thenReturn(services);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SERVICE_LIST;
        String actually = object.doGetCommand(request, response);
        assertEquals(expected, actually);

        verify(session, times(2)).setAttribute(anyString(), any());
    }

    @Test(expected = ParameterException.class)
    public void doGetCommandAuthorizedAdminWithBadParameter1() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("false");
        when(request.getParameter("searchName")).thenReturn("");

        when(servletContext.getAttribute("services")).thenReturn(services);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void doGetCommandAuthorizedAdminWithBadParameter2() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("false");
        when(request.getParameter("searchName")).thenReturn(null);

        when(servletContext.getAttribute("services")).thenReturn(services);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void doGetCommandAuthorizedCustomer() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void doGetCommandUnauthorizedCustomer() throws AppException {
        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

}