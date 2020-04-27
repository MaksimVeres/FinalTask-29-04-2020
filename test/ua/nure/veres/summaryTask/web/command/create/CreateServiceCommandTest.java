package ua.nure.veres.summaryTask.web.command.create;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.ee.data.DataSynchronizer;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.service.ServiceCreateException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateServiceCommandTest {

    @InjectMocks
    private CreateServiceCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private DaoFactory serviceDaoFactory;

    @Mock
    private Dao serviceDao;

    @Mock
    private Service service;

    @Mock
    private DataSynchronizer dataSynchronizer;

    @Mock
    private User user;

    @Before
    @SuppressWarnings("unchecked")
    public void init() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(serviceDaoFactory.getDao()).thenReturn(serviceDao);
        doNothing().when(dataSynchronizer).synchronizeServices();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("name")).thenReturn("testName");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(serviceDao.customReadOne(anyString(), any())).thenReturn(null);
        doNothing().when(serviceDao).create(any(Service.class));

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SERVICE_LIST;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(dataSynchronizer).synchronizeServices();
        verify(serviceDao).create(any(Service.class));
    }

    @Test(expected = ServiceCreateException.class)
    public void executeAuthorizedAdminWithWrongParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("name")).thenReturn("testName");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(serviceDao.customReadOne(anyString(), any())).thenReturn(service);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeAuthorizedCustomerWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("name")).thenReturn("testName");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorizedWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("name")).thenReturn("testName");

        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter1() throws AppException, IOException, ServletException {
        when(request.getParameter("name")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter2() throws AppException, IOException, ServletException {
        when(request.getParameter("name")).thenReturn("");

        object.execute(request, response);
    }

}