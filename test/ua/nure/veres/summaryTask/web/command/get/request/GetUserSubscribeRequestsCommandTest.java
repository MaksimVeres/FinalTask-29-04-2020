package ua.nure.veres.summaryTask.web.command.get.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserOrder;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GetUserSubscribeRequestsCommandTest {

    @InjectMocks
    private GetUserSubscribeRequestsCommand object;

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
    private TariffDaoFactory tariffDaoFactory;

    @Mock
    private ServiceDaoFactory serviceDaoFactory;

    @Mock
    private Dao userOrderDao;

    @Mock
    private Dao userDao;

    @Mock
    private Dao tariffDao;

    @Mock
    private Dao serviceDao;

    @Mock
    private UserOrder userOrder;

    @Mock
    private List<UserOrder> userOrders;

    @Mock
    private Iterator<UserOrder> userOrderIterator;

    @Mock
    private User dbUser;

    @Mock
    private Tariff tariff;

    @Mock
    private Service service;

    @Mock
    private User sessionUser;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(userOrderDaoFactory.getDao()).thenReturn(userOrderDao);
        when(userDaoFactory.getDao()).thenReturn(userDao);
        when(tariffDaoFactory.getDao()).thenReturn(tariffDao);
        when(serviceDaoFactory.getDao()).thenReturn(serviceDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void doGetCommandAuthorizedAdminWithRightParametersSearchAll() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);
        doNothing().when(session).setAttribute(anyString(), any());

        when(sessionUser.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("true");

        when(userOrderDao.read()).thenReturn(userOrders);

        when(userOrders.iterator()).thenReturn(userOrderIterator);

        when(userOrderIterator.hasNext()).thenReturn(true, false);
        when(userOrderIterator.next()).thenReturn(userOrder);

        when(userOrder.getUserId()).thenReturn(1L);
        when(userOrder.getTariffId()).thenReturn(1L);

        when(userDao.read(1L)).thenReturn(dbUser);

        when(tariffDao.read(1L)).thenReturn(tariff);

        when(tariff.getServiceId()).thenReturn(1L);

        when(serviceDao.read(1L)).thenReturn(service);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SUBSCRIBE_REQUESTS_PAGE;
        String actually = object.doGetCommand(request, response);
        assertEquals(expected, actually);

        verify(session, times(2)).setAttribute(anyString(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void doGetCommandAuthorizedAdminWithRightParametersSearchOne() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);
        doNothing().when(session).setAttribute(anyString(), any());

        when(sessionUser.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("false");
        when(request.getParameter("searchLogin")).thenReturn("testLogin");

        when(userOrderDao.customReadMany(anyString(), any())).thenReturn(userOrders);

        when(userOrders.iterator()).thenReturn(userOrderIterator);

        when(userOrderIterator.hasNext()).thenReturn(true, false);
        when(userOrderIterator.next()).thenReturn(userOrder);

        when(userOrder.getUserId()).thenReturn(1L);
        when(userOrder.getTariffId()).thenReturn(1L);

        when(userDao.read(1L)).thenReturn(dbUser);

        when(tariffDao.read(1L)).thenReturn(tariff);

        when(tariff.getServiceId()).thenReturn(1L);

        when(serviceDao.read(1L)).thenReturn(service);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SUBSCRIBE_REQUESTS_PAGE;
        String actually = object.doGetCommand(request, response);
        assertEquals(expected, actually);

        verify(session, times(2)).setAttribute(anyString(), any());
    }

    @Test(expected = CommandAccessException.class)
    public void doGetCommandAuthorizedCustomerWithRightParameters() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(1);

        object.doGetCommand(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void doGetCommandUnauthorizedWithRightParameters() throws AppException {
        when(session.getAttribute("user")).thenReturn(null);

        object.doGetCommand(request, response);
    }

    @Test(expected = ParameterException.class)
    public void doGetCommandAuthorizedAdminWithBadParameter1() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("false");
        when(request.getParameter("searchLogin")).thenReturn("");

        object.doGetCommand(request, response);
    }

    @Test(expected = ParameterException.class)
    public void doGetCommandAuthorizedAdminWithBadParameter2() throws AppException {
        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(0);

        when(request.getParameter("searchAll")).thenReturn("false");
        when(request.getParameter("searchLogin")).thenReturn(null);

        object.doGetCommand(request, response);
    }

}