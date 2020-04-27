package ua.nure.veres.summaryTask.web.command.describe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userDescribe.UserDescribeDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserDescribe;
import ua.nure.veres.summaryTask.db.entity.UserService;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.AttributeException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DescribeServiceCommandTest {

    @InjectMocks
    private DescribeServiceCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private UserDescribeDaoFactory userDescribeDaoFactory;

    @Mock
    private UserServiceDaoFactory userServiceDaoFactory;

    @Mock
    private UserDaoFactory userDaoFactory;

    @Mock
    private Dao userDescribeDao;

    @Mock
    private Dao userServiceDao;

    @Mock
    private Dao userDao;

    @Mock
    private User dbUser;

    @Mock
    private UserService userService;

    @Mock
    private User sessionUser;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(userDescribeDaoFactory.getDao()).thenReturn(userDescribeDao);
        when(userServiceDaoFactory.getDao()).thenReturn(userServiceDao);
        when(userDaoFactory.getDao()).thenReturn(userDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedWithRightParameters() throws AppException {
        when(session.getAttribute("userDescribeUserServiceId")).thenReturn("1");
        when(session.getAttribute("user")).thenReturn(sessionUser);
        doNothing().when(session).removeAttribute("userDescribeUserServiceId");
        doNothing().when(session).removeAttribute("userRequestTariff");
        doNothing().when(session).removeAttribute("userRequestService");

        when(sessionUser.getLogin()).thenReturn("testLogin");

        when(request.getParameter("phone")).thenReturn("testPhone");
        when(request.getParameter("comment")).thenReturn("testComment");

        when(userServiceDao.read(1L)).thenReturn(userService);

        when(userService.getUserId()).thenReturn(1L);

        when(userDao.read(1L)).thenReturn(dbUser);

        when(dbUser.getLogin()).thenReturn("testLogin");

        doNothing().when(userDescribeDao).create(any(UserDescribe.class));

        String expected = Path.MOVE_BY_REDIRECT_TO_DESCRIBE_SUBMITTED_PAGE;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(userDescribeDao).create(any(UserDescribe.class));
        verify(session).removeAttribute("userDescribeUserServiceId");
        verify(session).removeAttribute("userRequestTariff");
        verify(session).removeAttribute("userRequestService");
    }

    @Test(expected = CommandAccessException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedWithWrongParameter1() throws AppException {
        when(session.getAttribute("userDescribeUserServiceId")).thenReturn("1");
        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getLogin()).thenReturn("testLogin");

        when(request.getParameter("phone")).thenReturn("testPhone");
        when(request.getParameter("comment")).thenReturn("testComment");

        when(userServiceDao.read(1L)).thenReturn(userService);

        when(userService.getUserId()).thenReturn(1L);

        when(userDao.read(1L)).thenReturn(dbUser);

        when(dbUser.getLogin()).thenReturn("testAnotherLogin");

        object.execute(request, response);
    }

    @Test(expected = UserUpdateException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedWithWrongParameter2() throws AppException {
        when(session.getAttribute("userDescribeUserServiceId")).thenReturn("1");
        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(request.getParameter("phone")).thenReturn("testPhone");
        when(request.getParameter("comment")).thenReturn("testComment");

        when(userServiceDao.read(1L)).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorizedWithRightParameters() throws AppException {
        when(session.getAttribute("userDescribeUserServiceId")).thenReturn("1");
        when(session.getAttribute("user")).thenReturn(null);

        when(request.getParameter("phone")).thenReturn("testPhone");
        when(request.getParameter("comment")).thenReturn("testComment");

        object.execute(request, response);
    }

    @Test(expected = AttributeException.class)
    public void executeWithBadParameter1() throws AppException {
        when(session.getAttribute("userDescribeUserServiceId")).thenReturn("WRONG");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter2() throws AppException {
        when(session.getAttribute("userDescribeUserServiceId")).thenReturn("1");

        when(request.getParameter("phone")).thenReturn("");
        when(request.getParameter("comment")).thenReturn("testComment");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter3() throws AppException {
        when(session.getAttribute("userDescribeUserServiceId")).thenReturn("1");

        when(request.getParameter("phone")).thenReturn("");
        when(request.getParameter("comment")).thenReturn("testComment");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter4() throws AppException {
        when(session.getAttribute("userDescribeUserServiceId")).thenReturn("1");

        when(request.getParameter("phone")).thenReturn(null);
        when(request.getParameter("comment")).thenReturn("testComment");

        object.execute(request, response);
    }

    @Test(expected = AttributeException.class)
    public void executeWithBadParameter5() throws AppException {
        when(session.getAttribute("userDescribeUserServiceId")).thenReturn(null);

        object.execute(request, response);
    }

}