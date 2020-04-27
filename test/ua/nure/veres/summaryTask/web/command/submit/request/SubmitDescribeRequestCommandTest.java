package ua.nure.veres.summaryTask.web.command.submit.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.userDescribe.UserDescribeDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserDescribe;
import ua.nure.veres.summaryTask.db.entity.UserService;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.AttributeException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SubmitDescribeRequestCommandTest {

    @InjectMocks
    private SubmitDescribeRequestCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private UserServiceDaoFactory userServiceDaoFactory;

    @Mock
    private UserDescribeDaoFactory userDescribeDaoFactory;

    @Mock
    private Dao userServiceDao;

    @Mock
    private Dao userDescribeDao;

    @Mock
    private UserService userService;

    @Mock
    private UserDescribe userDescribe;

    @Mock
    private User user;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(userServiceDaoFactory.getDao()).thenReturn(userServiceDao);
        when(userDescribeDaoFactory.getDao()).thenReturn(userDescribeDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameters() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);
        when(session.getAttribute("replyDescribeRequest")).thenReturn("replyRequest");

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(user.getRoleId()).thenReturn(0);

        when(userDescribeDao.read(1L)).thenReturn(userDescribe);
        doNothing().when(userDescribeDao).delete(any(UserDescribe.class));

        when(userDescribe.getUserServiceId()).thenReturn(1L);

        when(userServiceDao.read(1L)).thenReturn(userService);
        doNothing().when(userServiceDao).delete(any(UserService.class));

        String expected = "replyRequest";
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(userServiceDao).delete(any(UserService.class));
        verify(userDescribeDao).delete(any(UserDescribe.class));
    }

    @Test(expected = AttributeException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameter1() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);
        when(session.getAttribute("replyDescribeRequest")).thenReturn(null);

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(user.getRoleId()).thenReturn(0);

        when(userDescribeDao.read(1L)).thenReturn(userDescribe);
        doNothing().when(userDescribeDao).delete(any(UserDescribe.class));

        when(userDescribe.getUserServiceId()).thenReturn(1L);

        when(userServiceDao.read(1L)).thenReturn(userService);
        doNothing().when(userServiceDao).delete(any(UserService.class));

        object.execute(request, response);

        verify(userServiceDao).delete(any(UserService.class));
        verify(userDescribeDao).delete(any(UserDescribe.class));
    }

    @Test(expected = ParameterException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameter2() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(user.getRoleId()).thenReturn(0);

        when(userDescribeDao.read(1L)).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeAuthorizedAdminWithBadParameter1() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("WRONG");

        when(user.getRoleId()).thenReturn(0);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeAuthorizedAdminWithBadParameter2() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("");

        when(user.getRoleId()).thenReturn(0);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeAuthorizedAdminWithBadParameter3() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn(null);

        when(user.getRoleId()).thenReturn(0);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeAuthorizedCustomer() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorizedCustomer() throws AppException {
        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

}