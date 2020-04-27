package ua.nure.veres.summaryTask.web.command.order;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserOrder;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.AttributeException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceCommandTest {

    @InjectMocks
    private OrderServiceCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private UserOrderDaoFactory userOrderDaoFactory;

    @Mock
    private UserDaoFactory userDaoFactory;

    @Mock
    private Dao userOrderDao;

    @Mock
    private Dao userDao;

    @Mock
    private User dbUser;

    @Mock
    private Tariff tariff;

    @Mock
    private User sessionUser;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(userOrderDaoFactory.getDao()).thenReturn(userOrderDao);
        when(userDaoFactory.getDao()).thenReturn(userDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedWithRightParameters() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);
        when(session.getAttribute("userRequestTariff")).thenReturn(tariff);
        doNothing().when(session).removeAttribute("userRequestTariff");
        doNothing().when(session).removeAttribute("userRequestService");

        when(sessionUser.getAccountState()).thenReturn(1000D);
        when(sessionUser.getStatusId()).thenReturn(0);

        when(tariff.getConnectionPayment()).thenReturn(100D);

        doNothing().when(userOrderDao).create(any(UserOrder.class));

        when(userDao.customReadOne(anyString(), any())).thenReturn(dbUser);
        doNothing().when(userDao).update(any(User.class));

        when(dbUser.getAccountState()).thenReturn(1000D);
        when(dbUser.getLogin()).thenReturn("testLogin");

        String expected = Path.MOVE_BY_REDIRECT_TO_ORDER_SUBMITTED_PAGE;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(userOrderDao).create(any(UserOrder.class));
        verify(dbUser).setAccountState(1000D - 100D);
        verify(userDao).update(any(User.class));
        verify(session).removeAttribute("userRequestTariff");
        verify(session).removeAttribute("userRequestService");
    }

    @Test(expected = UserUpdateException.class)
    public void executeAuthorizedWithWrongParameters() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);
        when(session.getAttribute("userRequestTariff")).thenReturn(tariff);

        when(sessionUser.getAccountState()).thenReturn(1000D);
        when(sessionUser.getStatusId()).thenReturn(0);

        when(tariff.getConnectionPayment()).thenReturn(100D);

        when(userDao.customReadOne(anyString(), any())).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = UserUpdateException.class)
    public void executeAuthorizedBlockedWithRightParameters() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);
        when(session.getAttribute("userRequestTariff")).thenReturn(tariff);

        when(sessionUser.getAccountState()).thenReturn(1000D);
        when(sessionUser.getStatusId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorized() throws AppException {
        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = AttributeException.class)
    public void executeAuthorizedWithBadParameter() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);
        when(session.getAttribute("userRequestTariff")).thenReturn(null);

        when(sessionUser.getStatusId()).thenReturn(0);

        object.execute(request, response);
    }

}