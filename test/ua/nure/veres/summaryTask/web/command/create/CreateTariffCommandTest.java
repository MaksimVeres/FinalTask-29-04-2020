package ua.nure.veres.summaryTask.web.command.create;

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
import ua.nure.veres.summaryTask.exception.tariff.TariffCreateException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateTariffCommandTest {

    @InjectMocks
    private CreateTariffCommand object;

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
    private Tariff tariff;

    @Mock
    private Service service;

    @Mock
    private User user;

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
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn("100");
        when(request.getParameter("monthPayment")).thenReturn("100");
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(tariffDao.customReadOne(anyString(), any())).thenReturn(null);
        doNothing().when(tariffDao).create(any(Tariff.class));

        when(serviceDao.read(1L)).thenReturn(service);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_TARIFF_LIST;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(tariffDao).create(any(Tariff.class));
    }

    @Test(expected = TariffCreateException.class)
    public void executeAuthorizedAdminWithWrongParameter1() throws AppException, IOException, ServletException {
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn("100");
        when(request.getParameter("monthPayment")).thenReturn("100");
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(tariffDao.customReadOne(anyString(), any())).thenReturn(tariff);

        object.execute(request, response);
    }

    @Test(expected = TariffCreateException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameter2() throws AppException, IOException, ServletException {
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn("100");
        when(request.getParameter("monthPayment")).thenReturn("100");
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(tariffDao.customReadOne(anyString(), any())).thenReturn(null);

        when(serviceDao.read(1L)).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeAuthorizedCustomerWithRightParameters() throws AppException, IOException, ServletException {
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
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn("100");
        when(request.getParameter("monthPayment")).thenReturn("100");
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameters1() throws AppException, IOException, ServletException {
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn("");
        when(request.getParameter("monthPayment")).thenReturn("100");
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameters2() throws AppException, IOException, ServletException {
        when(request.getParameter("name")).thenReturn("testName");
        when(request.getParameter("connectionPayment")).thenReturn(null);
        when(request.getParameter("monthPayment")).thenReturn(null);
        when(request.getParameter("feature")).thenReturn("testFeature");
        when(request.getParameter("serviceId")).thenReturn("15");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameters3() throws AppException, IOException, ServletException {
        when(request.getParameter("name")).thenReturn(null);
        when(request.getParameter("connectionPayment")).thenReturn(null);
        when(request.getParameter("monthPayment")).thenReturn(null);
        when(request.getParameter("feature")).thenReturn(null);
        when(request.getParameter("serviceId")).thenReturn(null);

        object.execute(request, response);
    }

}