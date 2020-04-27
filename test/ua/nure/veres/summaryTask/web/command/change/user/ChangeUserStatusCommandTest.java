package ua.nure.veres.summaryTask.web.command.change.user;

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
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChangeUserStatusCommandTest {

    @InjectMocks
    private ChangeUserStatusCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private DaoFactory userDaoFactory;

    @Mock
    private Dao userDao;

    @Mock
    private User sessionUser;

    @Mock
    private User dbUser;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(userDaoFactory.getDao()).thenReturn(userDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("statusId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(0);

        when(userDao.customReadOne(anyString(), any())).thenReturn(dbUser);
        doNothing().when(userDao).update(any(User.class));

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_USER_LIST;
        String actually = object.execute(request, response);

        assertEquals(expected, actually);

        verify(dbUser).setStatusId(1);
        verify(userDao).update(any(User.class));
    }

    @Test(expected = UserUpdateException.class)
    public void executeAuthorizedWithWrongParameter() throws AppException, IOException, ServletException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("statusId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(0);

        when(userDao.customReadOne(anyString(), any())).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeAuthorizedCustomerWithRightParameters() throws ServletException, IOException, AppException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("statusId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorizedWithRightParameters() throws ServletException, IOException, AppException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("statusId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeBadParameter1() throws ServletException, IOException, AppException {
        when(request.getParameter("login")).thenReturn(null);
        when(request.getParameter("statusId")).thenReturn("1");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeBadParameter2() throws ServletException, IOException, AppException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("statusId")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeBadParameter3() throws ServletException, IOException, AppException {
        when(request.getParameter("login")).thenReturn("");
        when(request.getParameter("statusId")).thenReturn("");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeBadParameter4() throws ServletException, IOException, AppException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("statusId")).thenReturn("WRONG_PARAMETER");

        object.execute(request, response);
    }

}