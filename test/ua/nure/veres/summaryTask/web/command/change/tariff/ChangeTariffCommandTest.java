package ua.nure.veres.summaryTask.web.command.change.tariff;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.tariff.TariffUpdateException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChangeTariffCommandTest {

    @InjectMocks
    private ChangeTariffCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private TariffDaoFactory tariffDaoFactory;

    @Mock
    private ServiceDaoFactory serviceDaoFactory;

    @Mock
    private Dao tariffDao;

    @Mock
    private Dao serviceDao;

    @Mock
    private User user;

    @Mock
    private Tariff tariff;

    @Mock
    private Service service;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);

        when(tariffDaoFactory.getDao()).thenReturn(tariffDao);
        when(serviceDaoFactory.getDao()).thenReturn(serviceDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn("100");
        when(request.getParameter("monthPayment")).thenReturn("100");
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(tariffDao.customReadOne("READ ONE name", "testName")).thenReturn(null);
        when(tariffDao.read(1L)).thenReturn(tariff);
        doNothing().when(tariffDao).update(any(Tariff.class));

        when(serviceDao.read(anyLong())).thenReturn(service);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_TARIFF_LIST;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(tariff).setName("testName");
        verify(tariff).setConnectionPayment(100d);
        verify(tariff).setMonthPayment(100d);
        verify(tariff).setFeature("testFeature");
        verify(tariff).setServiceId(1);

        verify(tariffDao).update(any(Tariff.class));

        verify(session).removeAttribute("changingTariff");
    }

    @Test(expected = TariffUpdateException.class)
    public void executeAuthorizedAdminWithWrongParameters1() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn("100");
        when(request.getParameter("monthPayment")).thenReturn("100");
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(tariffDao.customReadOne("READ ONE name", "testName")).thenReturn(tariff);

        when(tariff.getId()).thenReturn(1L);

        object.execute(request, response);
    }

    @Test(expected = TariffUpdateException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameters2() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn("100");
        when(request.getParameter("monthPayment")).thenReturn("100");
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(tariffDao.customReadOne("READ ONE name", "testName")).thenReturn(null);

        when(serviceDao.read(anyLong())).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = TariffUpdateException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameters3() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn("100");
        when(request.getParameter("monthPayment")).thenReturn("100");
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(tariffDao.customReadOne("READ ONE name", "testName")).thenReturn(null);
        when(tariffDao.read(1L)).thenReturn(null);

        when(serviceDao.read(anyLong())).thenReturn(service);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeAuthorizedCustomerWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn("100");
        when(request.getParameter("monthPayment")).thenReturn("100");
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorizedWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn("100");
        when(request.getParameter("monthPayment")).thenReturn("100");
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn(null);
        when(request.getParameter("name")).thenReturn(null);
        when(request.getParameter("connectionPayment")).thenReturn(null);
        when(request.getParameter("monthPayment")).thenReturn(null);
        when(request.getParameter("feature")).thenReturn(null);
        when(request.getParameter("serviceId")).thenReturn(null);

        object.execute(request, response);
    }


}