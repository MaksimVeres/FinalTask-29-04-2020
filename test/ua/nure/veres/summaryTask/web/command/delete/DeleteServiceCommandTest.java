package ua.nure.veres.summaryTask.web.command.delete;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.userDescribe.UserDescribeDaoFactory;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.*;
import ua.nure.veres.summaryTask.ee.data.DataSynchronizer;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeleteServiceCommandTest {

    @InjectMocks
    private DeleteServiceCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServiceDaoFactory serviceDaoFactory;

    @Mock
    private TariffDaoFactory tariffDaoFactory;

    @Mock
    private UserDescribeDaoFactory userDescribeDaoFactory;

    @Mock
    private UserOrderDaoFactory userOrderDaoFactory;

    @Mock
    private UserServiceDaoFactory userServiceDaoFactory;

    @Mock
    private DataSynchronizer dataSynchronizer;

    @Mock
    private Dao serviceDao;

    @Mock
    private Dao tariffDao;

    @Mock
    private Dao userDescribeDao;

    @Mock
    private Dao userOrderDao;

    @Mock
    private Dao userServiceDao;

    @Mock
    private Service service;

    @Mock
    private Tariff tariff;

    @Mock
    private List<Tariff> tariffs;

    @Mock
    private Iterator<Tariff> tariffIterator;

    @Mock
    private UserDescribe userDescribe;

    @Mock
    private List<UserDescribe> userDescribes;

    @Mock
    private Iterator<UserDescribe> userDescribeIterator;

    @Mock
    private UserOrder userOrder;

    @Mock
    private List<UserOrder> userOrders;

    @Mock
    private Iterator<UserOrder> userOrderIterator;

    @Mock
    private UserService userService;

    @Mock
    private List<UserService> userServices;

    @Mock
    private Iterator<UserService> userServiceIterator;

    @Mock
    private User user;

    @Before
    @SuppressWarnings("unchecked")
    public void init() throws AppException {
        when(request.getSession()).thenReturn(session);
        when(serviceDaoFactory.getDao()).thenReturn(serviceDao);
        when(tariffDaoFactory.getDao()).thenReturn(tariffDao);
        when(userDescribeDaoFactory.getDao()).thenReturn(userDescribeDao);
        when(userOrderDaoFactory.getDao()).thenReturn(userOrderDao);
        when(userServiceDaoFactory.getDao()).thenReturn(userServiceDao);
        doNothing().when(dataSynchronizer).synchronizeServices();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameters1() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(serviceDao.read(1L)).thenReturn(service);
        doNothing().when(serviceDao).delete(any(Service.class));

        when(tariffDao.customReadMany(anyString(), any())).thenReturn(tariffs);
        doNothing().when(tariffDao).delete(any(Tariff.class));

        when(tariffs.iterator()).thenReturn(tariffIterator);

        when(tariffIterator.hasNext()).thenReturn(true, false);
        when(tariffIterator.next()).thenReturn(tariff);

        when(userDescribeDao.customReadMany(anyString(), any())).thenReturn(userDescribes);

        when(userDescribes.iterator()).thenReturn(userDescribeIterator);

        when(userDescribeIterator.hasNext()).thenReturn(true, false);
        when(userDescribeIterator.next()).thenReturn(userDescribe);

        doNothing().when(userDescribeDao).delete(any(UserDescribe.class));

        when(userOrderDao.customReadMany(anyString(), any())).thenReturn(userOrders);

        when(userOrders.iterator()).thenReturn(userOrderIterator);

        when(userOrderIterator.hasNext()).thenReturn(true, false);
        when(userOrderIterator.next()).thenReturn(userOrder);

        doNothing().when(userOrderDao).delete(any(UserOrder.class));

        when(userServiceDao.customReadMany(anyString(), any())).thenReturn(userServices);
        when(userServices.iterator()).thenReturn(userServiceIterator);

        when(userServiceIterator.hasNext()).thenReturn(true, false);
        when(userServiceIterator.next()).thenReturn(userService);

        doNothing().when(userServiceDao).delete(any(UserService.class));

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SERVICE_LIST;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(tariffDao).delete(any(Tariff.class));
        verify(userDescribeDao).delete(any(UserDescribe.class));
        verify(userOrderDao).delete(any(UserOrder.class));
        verify(userServiceDao).delete(any(UserService.class));
        verify(serviceDao).delete(any(Service.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameters2() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(serviceDao.read(1L)).thenReturn(null);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SERVICE_LIST;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);
    }

    @Test(expected = CommandAccessException.class)
    public void executeAuthorizedCustomerWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorizedWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executedWithBadParameter1() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("");

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executedWithBadParameter2() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("WRONG");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executedWithBadParameter3() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn(null);

        object.execute(request, response);
    }

}