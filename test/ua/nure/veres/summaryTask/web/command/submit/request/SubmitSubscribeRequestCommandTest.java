package ua.nure.veres.summaryTask.web.command.submit.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserOrder;
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
public class SubmitSubscribeRequestCommandTest {

    @InjectMocks
    private SubmitSubscribeRequestCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private UserOrderDaoFactory userOrderDaoFactory;

    @Mock
    private UserServiceDaoFactory userServiceDaoFactory;

    @Mock
    private Dao userServiceDao;

    @Mock
    private Dao userOrderDao;

    @Mock
    private UserOrder userOrder;

    @Mock
    private User user;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(userServiceDaoFactory.getDao()).thenReturn(userServiceDao);
        when(userOrderDaoFactory.getDao()).thenReturn(userOrderDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameters() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);
        when(session.getAttribute("replySubscribeRequest")).thenReturn("replyRequest");

        when(user.getRoleId()).thenReturn(0);

        when(request.getParameter("userRequestId")).thenReturn("1");
        when(request.getParameter("address")).thenReturn("testAddress");

        when(userOrderDao.read(1L)).thenReturn(userOrder);
        doNothing().when(userOrderDao).delete(any(UserOrder.class));

        doNothing().when(userServiceDao).create(any(UserService.class));

        String expected = "replyRequest";
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(userServiceDao).create(any(UserService.class));
        verify(userOrderDao).delete(any(UserOrder.class));
    }

    @Test(expected = AttributeException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameter1() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(user.getRoleId()).thenReturn(0);

        when(request.getParameter("userRequestId")).thenReturn("1");
        when(request.getParameter("address")).thenReturn("testAddress");

        when(userOrderDao.read(1L)).thenReturn(userOrder);
        doNothing().when(userOrderDao).delete(any(UserOrder.class));

        doNothing().when(userServiceDao).create(any(UserService.class));

        String expected = "replyRequest";
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(userServiceDao).create(any(UserService.class));
        verify(userOrderDao).delete(any(UserOrder.class));
    }

    @Test(expected = ParameterException.class)
    public void executeAuthorizedAdminWithWrongParameter2() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(user.getRoleId()).thenReturn(0);

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