package ua.nure.veres.summaryTask.web.command.decline.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserOrder;
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
public class DeclineSubscribeRequestCommandTest {

    @InjectMocks
    private DeclineSubscribeRequestCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private UserOrderDaoFactory userOrderDaoFactory;

    @Mock
    private TariffDaoFactory tariffDaoFactory;

    @Mock
    private UserDaoFactory userDaoFactory;

    @Mock
    private Dao userOrderDao;

    @Mock
    private Dao tariffDao;

    @Mock
    private Dao userDao;

    @Mock
    private UserOrder userOrder;

    @Mock
    private Tariff tariff;

    @Mock
    private User user;


    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(userOrderDaoFactory.getDao()).thenReturn(userOrderDao);
        when(tariffDaoFactory.getDao()).thenReturn(tariffDao);
        when(userDaoFactory.getDao()).thenReturn(userDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameters() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);
        when(session.getAttribute("replySubscribeRequest")).thenReturn("someReplyRequest");

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(userOrderDao.read(1L)).thenReturn(userOrder);
        doNothing().when(userOrderDao).delete(any(UserOrder.class));

        when(userOrder.getTariffId()).thenReturn(1L);
        when(userOrder.getUserId()).thenReturn(1L);

        when(tariffDao.read(1L)).thenReturn(tariff);

        when(tariff.getConnectionPayment()).thenReturn(100d);

        when(userDao.read(1L)).thenReturn(user);
        doNothing().when(userDao).update(any(User.class));

        when(user.getAccountState()).thenReturn(100d);

        String expected = "someReplyRequest";
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(user).setAccountState(100d + 100d);
        verify(userDao).update(any(User.class));
        verify(userOrderDao).delete(any(UserOrder.class));
    }

    @Test(expected = AttributeException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameter1() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);
        when(session.getAttribute("replySubscribeRequest")).thenReturn(null);

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(userOrderDao.read(1L)).thenReturn(userOrder);
        doNothing().when(userOrderDao).delete(any(UserOrder.class));

        when(userOrder.getTariffId()).thenReturn(1L);
        when(userOrder.getUserId()).thenReturn(1L);

        when(tariffDao.read(1L)).thenReturn(tariff);

        when(tariff.getConnectionPayment()).thenReturn(100d);

        when(userDao.read(1L)).thenReturn(user);
        doNothing().when(userDao).update(any(User.class));

        when(user.getAccountState()).thenReturn(100d);

        object.execute(request, response);

        verify(user).setAccountState(100d + 100d);
        verify(userDao).update(any(User.class));
        verify(userOrderDao).delete(any(UserOrder.class));
    }

    @Test(expected = ParameterException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameter2() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(userOrderDao.read(1L)).thenReturn(userOrder);

        when(userOrder.getTariffId()).thenReturn(1L);
        when(userOrder.getUserId()).thenReturn(1L);

        when(tariffDao.read(1L)).thenReturn(tariff);

        when(tariff.getConnectionPayment()).thenReturn(100d);

        when(userDao.read(1L)).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameter3() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(userOrderDao.read(1L)).thenReturn(userOrder);

        when(userOrder.getTariffId()).thenReturn(1L);

        when(tariffDao.read(1L)).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameter4() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(userOrderDao.read(1L)).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeAuthorizedAdminWithBadParameter1() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("WRONG");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeAuthorizedAdminWithBadParameter2() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeAuthorizedAdminWithBadParameter3() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn(null);

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