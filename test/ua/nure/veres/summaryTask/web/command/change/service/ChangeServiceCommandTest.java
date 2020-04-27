package ua.nure.veres.summaryTask.web.command.change.service;

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
import ua.nure.veres.summaryTask.exception.service.ServiceUpdateException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChangeServiceCommandTest {

    @InjectMocks
    private ChangeServiceCommand object;

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
    private User user;

    @Mock
    private DataSynchronizer dataSynchronizer;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(serviceDaoFactory.getDao()).thenReturn(serviceDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("testName");

        when(session.getAttribute("user")).thenReturn(user);
        when(session.getAttribute("replyRequest")).thenReturn(null);

        when(user.getRoleId()).thenReturn(0);

        when(serviceDao.customReadOne(anyString(), any())).thenReturn(null);
        when(serviceDao.read(1L)).thenReturn(service);
        doNothing().when(serviceDao).update(any(Service.class));

        doNothing().when(dataSynchronizer).synchronizeServices();

        assertEquals(Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_SERVICE_LIST, object.execute(request, response));

        verify(service).setName("testName");
        verify(serviceDao).update(any(Service.class));
        verify(dataSynchronizer).synchronizeServices();
    }

    @Test(expected = ServiceUpdateException.class)
    public void executeAuthorizedAdminWithWrongParameters1() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("testName");

        when(session.getAttribute("user")).thenReturn(user);

        object.execute(request, response);
    }

    @Test(expected = ServiceUpdateException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameters2() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("testName");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(0);

        when(serviceDao.customReadOne(anyString(), any())).thenReturn(null);
        when(serviceDao.read(1L)).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeAuthorizedCustomerWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("testName");

        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorizedWithRightParameters() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("testName");

        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter1() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn(null);
        when(request.getParameter("name")).thenReturn("testName");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter2() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn(null);

        object.execute(request, response);
    }
}