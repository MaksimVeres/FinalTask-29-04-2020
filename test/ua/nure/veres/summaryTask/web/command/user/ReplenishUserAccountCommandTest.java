package ua.nure.veres.summaryTask.web.command.user;

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
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReplenishUserAccountCommandTest {

    @InjectMocks
    private ReplenishUserAccountCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private DaoFactory userDaoFactory;

    @Mock
    private Dao userDao;

    @Mock
    private User user;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(userDaoFactory.getDao()).thenReturn(userDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("value")).thenReturn("100");

        when(userDao.customReadOne(anyString(), any())).thenReturn(user);
        doNothing().when(userDao).update(any(User.class));

        when(user.getAccountState()).thenReturn(0d);
        doNothing().when(user).setAccountState(0d + 100d);

        String expected = Path.MOVE_BY_REDIRECT_TO_ACCOUNT_REPLENISHED_PAGE;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(user).setAccountState(0d + 100d);
        verify(userDao).update(any(User.class));
    }

    @Test(expected = UserUpdateException.class)
    public void executeWithWrongParameter() throws AppException, IOException, ServletException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("value")).thenReturn("100");

        when(userDao.customReadOne(anyString(), any())).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter1() throws AppException, IOException, ServletException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("value")).thenReturn("WRONG");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter2() throws AppException, IOException, ServletException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("value")).thenReturn("");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter3() throws AppException, IOException, ServletException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("value")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter4() throws AppException, IOException, ServletException {
        when(request.getParameter("login")).thenReturn("");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter5() throws AppException, IOException, ServletException {
        when(request.getParameter("login")).thenReturn(null);

        object.execute(request, response);
    }

}