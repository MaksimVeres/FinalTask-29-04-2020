package ua.nure.veres.summaryTask.web.command.user;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserService;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateUserServicesCommandTest {

    @InjectMocks
    private UpdateUserServicesCommand object;

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
    private UserServiceDaoFactory userServiceDaoFactory;

    @Mock
    private Dao serviceDao;

    @Mock
    private Dao tariffDao;

    @Mock
    private Dao userServiceDao;

    @Mock
    private Service service;

    @Mock
    private Tariff tariff;

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
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(serviceDaoFactory.getDao()).thenReturn(serviceDao);
        when(tariffDaoFactory.getDao()).thenReturn(tariffDao);
        when(userServiceDaoFactory.getDao()).thenReturn(userServiceDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeWithRightParameters() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);
        doNothing().when(session).setAttribute(anyString(), any());

        when(userServiceDao.customReadMany(anyString(), any())).thenReturn(userServices);

        when(userServices.iterator()).thenReturn(userServiceIterator);

        when(userServiceIterator.hasNext()).thenReturn(true, false);
        when(userServiceIterator.next()).thenReturn(userService);

        when(userService.getTariffId()).thenReturn(1L);

        when(tariffDao.read(1L)).thenReturn(tariff);

        when(tariff.getServiceId()).thenReturn(1L);

        when(serviceDao.read(1L)).thenReturn(service);

        String expected = Path.MOVE_BY_FORWARD_TO_PERSONAL_CABINET_SERVICES_PAGE;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(session).setAttribute(anyString(), any());
    }

    @Test(expected = CommandAccessException.class)
    public void executeWithWrongParameter() throws AppException {
        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

}