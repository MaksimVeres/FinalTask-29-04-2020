package ua.nure.veres.summaryTask.web.command.submit.register;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.ee.security.SecurityManager;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.registration.RegisterException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SubmitRegisterCommandTest {

    @InjectMocks
    private SubmitRegisterCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private DaoFactory userDaoFactory;

    @Mock
    private SecurityManager securityManager;

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
    public void executeWithRightParameters() throws AppException, NoSuchAlgorithmException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("password")).thenReturn("testPassword");

        when(userDao.customReadOne(anyString(), any())).thenReturn(null);
        doNothing().when(userDao).create(any(User.class));

        when(securityManager.hashString("testPassword")).thenCallRealMethod();

        String expected = Path.COMMAND_LOGIN;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(userDao).create(any(User.class));
    }

    @Test(expected = RegisterException.class)
    public void executeWithWrongParameter() throws AppException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("password")).thenReturn("testPassword");

        when(userDao.customReadOne(anyString(), any())).thenReturn(user);
        object.execute(request, response);
    }

    @Test(expected = RegisterException.class)
    public void executeWithBadParameter1() throws AppException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("password")).thenReturn("");

        object.execute(request, response);
    }

    @Test(expected = RegisterException.class)
    public void executeWithBadParameter2() throws AppException {
        when(request.getParameter("login")).thenReturn("testLogin");
        when(request.getParameter("password")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = RegisterException.class)
    public void executeWithBadParameter3() throws AppException {
        when(request.getParameter("login")).thenReturn("");

        object.execute(request, response);
    }

    @Test(expected = RegisterException.class)
    public void executeWithBadParameter4() throws AppException {
        when(request.getParameter("login")).thenReturn(null);

        object.execute(request, response);
    }

}