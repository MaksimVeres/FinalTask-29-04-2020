package ua.nure.veres.summaryTask.web.command.get.user;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetUsersCommandTest {

    @InjectMocks
    private GetUsersCommand object;

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
    private User dbUser;

    @Mock
    private ArrayList<User> users;

    @Mock
    private Iterator<User> userIterator;

    @Mock
    private User sessionUser;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(tariffDaoFactory.getDao()).thenReturn(tariffDao);
    }

    @Test
    public void doGetCommandAuthorizedAdminWithRightParametersSearchAll() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);
        doNothing().when(session).setAttribute(anyString(), any());

        when(sessionUser.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("true");

        when(tariffDao.read()).thenReturn(users);

        when(users.iterator()).thenReturn(userIterator);

        when(userIterator.hasNext()).thenReturn(true, false);
        when(userIterator.next()).thenReturn(dbUser);

        when(users.size()).thenReturn(1);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_USER_LIST;
        String actually = object.doGetCommand(request, response);
        assertEquals(expected, actually);

        verify(session, times(2)).setAttribute(anyString(), any());
    }

    @Test
    public void doGetCommandAuthorizedAdminWithRightParametersSearchOne() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);
        doNothing().when(session).setAttribute(anyString(), any());

        when(sessionUser.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("false");
        when(request.getParameter("searchLogin")).thenReturn("testLogin");

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_USER_LIST;
        String actually = object.doGetCommand(request, response);
        assertEquals(expected, actually);

        verify(session, times(2)).setAttribute(anyString(), any());
    }

    @Test(expected = ParameterException.class)
    public void doGetCommandAuthorizedAdminWithBadParameter1() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("false");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void doGetCommandAuthorizedAdminWithBadParameter2() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("false");
        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void doGetCommandAuthorizedCustomer() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void doGetCommandUnauthorizedCustomer() throws AppException {
        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }
}