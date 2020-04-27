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
import ua.nure.veres.summaryTask.ee.security.SecurityManager;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChangePasswordCommandTest {

    @InjectMocks
    private ChangePasswordCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServletContext context;

    @Mock
    private DaoFactory daoFactory;

    @Mock
    private Dao userDao;

    @Mock
    private User sessionUser;

    @Mock
    private User dbUser;

    @Mock
    private SecurityManager securityManager;

    @Before
    @SuppressWarnings("unchecked")
    public void init()  {
        when(request.getSession()).thenReturn(session);
        when(request.getServletContext()).thenReturn(context);

        when(daoFactory.getDao()).thenReturn(userDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedWithRightParameters() throws AppException, NoSuchAlgorithmException {
        when(request.getParameter("newPassword")).thenReturn("testNewPassword");
        when(request.getParameter("oldPassword")).thenReturn("testPassword");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getLogin()).thenReturn("testLogin");
        when(sessionUser.getPassword()).thenReturn("fed3b61b26081849378080b34e693d2e");

        when(userDao.customReadOne("READ ONE login", "testLogin")).thenReturn(dbUser);
        doNothing().when(userDao).update(any(User.class));

        when(context.getAttribute("LOGGED_USER: testLogin")).thenReturn(sessionUser);

        when(securityManager.checkPassword(anyString(), any(User.class))).thenCallRealMethod();
        when(securityManager.hashString(anyString())).thenCallRealMethod();

        String expected = Path.MOVE_BY_REDIRECT_TO_PERSONAL_CABINET_PAGE;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(dbUser).setPassword("7BC9EF3CA017FB37CA3FE4DC76F0B3E2");
        verify(userDao).update(any(User.class));
    }

    @Test(expected = UserUpdateException.class)
    public void executeAuthorizedWithWrongParameters1() throws AppException {
        when(request.getParameter("newPassword")).thenReturn("testNewPassword");
        when(request.getParameter("oldPassword")).thenReturn("testWrongPassword");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getLogin()).thenReturn("testLogin");
        when(sessionUser.getPassword()).thenReturn("fed3b61b26081849378080b34e693d2e");

        when(context.getAttribute("LOGGED_USER: testLogin")).thenReturn(sessionUser);

        when(securityManager.checkPassword(anyString(), any(User.class))).thenCallRealMethod();

        object.execute(request, response);
    }

    @Test(expected = UserUpdateException.class)
    public void executeAuthorizedWithWrongParameters2() throws AppException, NoSuchAlgorithmException {
        when(request.getParameter("newPassword")).thenReturn("testNewPassword");
        when(request.getParameter("oldPassword")).thenReturn("testPassword");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getLogin()).thenReturn("testLogin");
        when(sessionUser.getPassword()).thenReturn("fed3b61b26081849378080b34e693d2e");

        when(userDao.customReadOne("READ ONE login", "testLogin")).thenReturn(null);

        when(context.getAttribute("LOGGED_USER: testLogin")).thenReturn(sessionUser);

        when(securityManager.checkPassword(anyString(), any(User.class))).thenCallRealMethod();
        when(securityManager.hashString(anyString())).thenCallRealMethod();

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorizedWithRightParameters() throws AppException {
        when(request.getParameter("newPassword")).thenReturn("testNewPassword");
        when(request.getParameter("oldPassword")).thenReturn("testPassword");

        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeBadParameter1() throws AppException {
        when(request.getParameter("newPassword")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeBadParameter2() throws AppException {
        when(request.getParameter("newPassword")).thenReturn("testNewPassword");
        when(request.getParameter("oldPassword")).thenReturn(null);

        object.execute(request, response);
    }
}