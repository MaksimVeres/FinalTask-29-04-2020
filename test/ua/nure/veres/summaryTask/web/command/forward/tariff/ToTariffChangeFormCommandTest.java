package ua.nure.veres.summaryTask.web.command.forward.tariff;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.forward.ForwardCommandException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.tariff.TariffUpdateException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ToTariffChangeFormCommandTest {

    @InjectMocks
    private ToTariffChangeFormCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private DaoFactory tariffDaoFactory;

    @Mock
    private Dao tariffDao;

    @Mock
    private Tariff tariff;

    @Mock
    private User user;

    @Before
    @SuppressWarnings("unchecked")
    public void init()  {
        when(request.getSession()).thenReturn(session);
        when(tariffDaoFactory.getDao()).thenReturn(tariffDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameters() throws AppException {
        when(request.getParameter("forwardMethod")).thenReturn("forward");
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);
        doNothing().when(session).setAttribute(anyString(), any());

        when(user.getRoleId()).thenReturn(0);

        when(tariffDao.read(1L)).thenReturn(tariff);

        String expected = Path.MOVE_BY_FORWARD_TO_ADMINISTRATION_CHANGE_TARIFF_FORM;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(session).setAttribute(anyString(), any());
    }

    @Test(expected = TariffUpdateException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameter1() throws AppException {
        when(request.getParameter("forwardMethod")).thenReturn("forward");
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(tariffDao.read(1L)).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithBadParameter() throws AppException {
        when(request.getParameter("forwardMethod")).thenReturn("forward");
        when(request.getParameter("id")).thenReturn("WRONG");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeAuthorizedCustomerWithRightParameters() throws AppException {
        when(request.getParameter("forwardMethod")).thenReturn("forward");
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorizedWithRightParameters() throws AppException {
        when(request.getParameter("forwardMethod")).thenReturn("forward");
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter1() throws AppException {
        when(request.getParameter("forwardMethod")).thenReturn("forward");
        when(request.getParameter("id")).thenReturn("");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter2() throws AppException {
        when(request.getParameter("forwardMethod")).thenReturn("forward");
        when(request.getParameter("id")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ForwardCommandException.class)
    public void executeWithBadParameter3() throws AppException {
        when(request.getParameter("forwardMethod")).thenReturn("redirect");

        object.execute(request, response);
    }

}