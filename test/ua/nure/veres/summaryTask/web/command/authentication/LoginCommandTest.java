package ua.nure.veres.summaryTask.web.command.authentication;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.ee.PaymentManager;
import ua.nure.veres.summaryTask.ee.security.SecurityManager;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.authentication.LogInException;
import ua.nure.veres.summaryTask.exception.user.UserIdentityException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@RunWith(MockitoJUnitRunner.class)
public class LoginCommandTest {

    @InjectMocks
    private LoginCommand object;

    @Mock
    private UserDaoFactory userDaoFactory;

    @Mock
    private Dao userDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private SecurityManager securityManager;

    @Mock
    private PaymentManager paymentManager;

    @Mock
    private User user;


    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        Mockito.when(userDaoFactory.getDao()).thenReturn(userDao);
        Mockito.when(request.getSession()).thenReturn(session);
    }


    @Test
    public void executeRightParameters() throws AppException {
        Mockito.when(request.getParameter("login")).thenReturn("testLogin");
        Mockito.when(request.getParameter("password")).thenReturn("testPassword");
        Mockito.when(userDao.customReadOne(Mockito.anyString(), Mockito.any())).thenReturn(user);

        Mockito.when(user.getLogin()).thenReturn("testLogin");
        Mockito.when(user.getRoleId()).thenReturn(1);

        Mockito.when(securityManager.checkPassword("testPassword", user)).thenReturn(true);
        Mockito.when(securityManager.getLoggedUser("testLogin")).thenReturn(null);

        String path = object.execute(request, response);

        Assert.assertEquals(Path.COMMAND_REDIRECT_TO_MAIN_PAGE, path);

        Mockito.verify(paymentManager).checkEconomicStability(user);
        Mockito.verify(securityManager).putLoggedUser(user);
    }

    @Test(expected = LogInException.class)
    public void executeWrongParameters() throws AppException {
        Mockito.when(request.getParameter("login")).thenReturn("testLogin");
        Mockito.when(request.getParameter("password")).thenReturn("testPassword");
        Mockito.when(userDao.customReadOne(Mockito.anyString(), Mockito.any())).thenReturn(null);
        object.execute(request, response);
    }

    @Test(expected = UserIdentityException.class)
    public void executeBadParameters() throws AppException {
        Mockito.when(request.getParameter("login")).thenReturn(null);
        Mockito.when(request.getParameter("password")).thenReturn(null);
        object.execute(request, response);
    }

}