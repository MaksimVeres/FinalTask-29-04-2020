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
import ua.nure.veres.summaryTask.dao.userDescribe.UserDescribeDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.*;
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
public class GetUserDescribeRequestsCommandTest {

    @InjectMocks
    private GetUserDescribeRequestsCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private UserDescribeDaoFactory userDescribeDaoFactory;

    @Mock
    private UserServiceDaoFactory userServiceDaoFactory;

    @Mock
    private UserDaoFactory userDaoFactory;

    @Mock
    private TariffDaoFactory tariffDaoFactory;

    @Mock
    private ServiceDaoFactory serviceDaoFactory;

    @Mock
    private Dao userDescribeDao;

    @Mock
    private Dao userServiceDao;

    @Mock
    private Dao userDao;

    @Mock
    private Dao tariffDao;

    @Mock
    private Dao serviceDao;

    @Mock
    private UserDescribe userDescribe;

    @Mock
    private List<UserDescribe> userDescribes;

    @Mock
    private Iterator<UserDescribe> userDescribeIterator;

    @Mock
    private UserService userService;

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
        when(userDescribeDaoFactory.getDao()).thenReturn(userDescribeDao);
        when(userServiceDaoFactory.getDao()).thenReturn(userServiceDao);
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

        when(userDescribeDao.read()).thenReturn(userDescribes);

        when(userDescribes.iterator()).thenReturn(userDescribeIterator);

        when(userDescribeIterator.hasNext()).thenReturn(true, false);
        when(userDescribeIterator.next()).thenReturn(userDescribe);

        when(userDescribe.getUserServiceId()).thenReturn(1L);

        when(userServiceDao.read(1L)).thenReturn(userService);

        when(userService.getUserId()).thenReturn(1L);
        when(userService.getTariffId()).thenReturn(1L);

        when(userDao.read(1L)).thenReturn(dbUser);

        when(tariffDao.read(1L)).thenReturn(tariff);

        when(tariff.getServiceId()).thenReturn(1L);

        when(serviceDao.read(1L)).thenReturn(service);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_DESCRIBE_REQUESTS_PAGE;
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

        when(userDescribeDao.customReadMany(anyString(), any())).thenReturn(userDescribes);

        when(userDescribes.iterator()).thenReturn(userDescribeIterator);

        when(userDescribeIterator.hasNext()).thenReturn(true, false);
        when(userDescribeIterator.next()).thenReturn(userDescribe);

        when(userDescribe.getUserServiceId()).thenReturn(1L);

        when(userServiceDao.read(1L)).thenReturn(userService);

        when(userService.getUserId()).thenReturn(1L);
        when(userService.getTariffId()).thenReturn(1L);

        when(userDao.read(1L)).thenReturn(dbUser);

        when(tariffDao.read(1L)).thenReturn(tariff);

        when(tariff.getServiceId()).thenReturn(1L);

        when(serviceDao.read(1L)).thenReturn(service);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_DESCRIBE_REQUESTS_PAGE;
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