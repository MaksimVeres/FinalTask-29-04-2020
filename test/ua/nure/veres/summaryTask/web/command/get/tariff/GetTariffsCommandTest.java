package ua.nure.veres.summaryTask.web.command.get.tariff;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GetTariffsCommandTest {

    @InjectMocks
    private GetTariffsCommand object;

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
    private ArrayList<Tariff> tariffs;

    @Mock
    private User user;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(tariffDaoFactory.getDao()).thenReturn(tariffDao);
    }

    @Test
    public void doGetCommandAuthorizedAdminWithRightParametersSearchAll() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);
        doNothing().when(session).setAttribute(anyString(), any());

        when(user.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("true");

        when(tariffDao.read()).thenReturn(tariffs);

        when(tariffs.size()).thenReturn(1);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_TARIFF_LIST;
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

        when(tariffDao.read()).thenReturn(tariffs);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_TARIFF_LIST;
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

        when(tariffDao.read()).thenReturn(tariffs);

        when(tariffs.size()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void doGetCommandAuthorizedAdminWithBadParameter2() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("false");
        when(request.getParameter("searchName")).thenReturn(null);

        when(tariffDao.read()).thenReturn(tariffs);

        when(tariffs.size()).thenReturn(1);

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