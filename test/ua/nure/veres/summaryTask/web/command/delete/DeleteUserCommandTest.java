package ua.nure.veres.summaryTask.web.command.delete;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.user.UserDaoFactory;
import ua.nure.veres.summaryTask.dao.userDescribe.UserDescribeDaoFactory;
import ua.nure.veres.summaryTask.dao.userOrder.UserOrderDaoFactory;
import ua.nure.veres.summaryTask.dao.userService.UserServiceDaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserDescribe;
import ua.nure.veres.summaryTask.db.entity.UserOrder;
import ua.nure.veres.summaryTask.db.entity.UserService;
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
public class DeleteUserCommandTest {

    @InjectMocks
    private DeleteUserCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private UserDaoFactory userDaoFactory;

    @Mock
    private UserServiceDaoFactory userServiceDaoFactory;

    @Mock
    private UserOrderDaoFactory userOrderDaoFactory;

    @Mock
    private UserDescribeDaoFactory userDescribeDaoFactory;

    @Mock
    private Dao userDao;

    @Mock
    private Dao userServiceDao;

    @Mock
    private List<UserService> userServices;

    @Mock
    private Iterator<UserService> userServiceIterator;

    @Mock
    private Dao userOrderDao;

    @Mock
    private Dao userDescribeDao;

    @Mock
    private User dbUser;

    @Mock
    private UserService userService;

    @Mock
    private UserOrder userOrder;

    @Mock
    private List<UserOrder> userOrders;

    @Mock
    private Iterator<UserOrder> userOrderIterator;

    @Mock
    private UserDescribe userDescribe;

    @Mock
    private List<UserDescribe> userDescribes;

    @Mock
    private Iterator<UserDescribe> userDescribeIterator;

    @Mock
    private User sessionUser;

    @Before
    @SuppressWarnings("unchecked")
    public void init()  {
        when(request.getSession()).thenReturn(session);
        when(userDaoFactory.getDao()).thenReturn(userDao);
        when(userServiceDaoFactory.getDao()).thenReturn(userServiceDao);
        when(userOrderDaoFactory.getDao()).thenReturn(userOrderDao);
        when(userDescribeDaoFactory.getDao()).thenReturn(userDescribeDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameter1() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(0);

        when(userDao.read(1L)).thenReturn(dbUser);
        doNothing().when(userDao).delete(any(User.class));

        when(dbUser.getLogin()).thenReturn("testLogin");

        when(userServiceDao.customReadMany(anyString(), any())).thenReturn(userServices);
        doNothing().when(userServiceDao).delete(any(UserService.class));

        when(userServices.iterator()).thenReturn(userServiceIterator);

        when(userServiceIterator.hasNext()).thenReturn(true, false);
        when(userServiceIterator.next()).thenReturn(userService);

        when(userOrderDao.customReadMany(anyString(), any())).thenReturn(userOrders);
        doNothing().when(userOrderDao).delete(any(UserOrder.class));

        when(userOrders.iterator()).thenReturn(userOrderIterator);

        when(userOrderIterator.hasNext()).thenReturn(true, false);
        when(userOrderIterator.next()).thenReturn(userOrder);

        when(userDescribeDao.customReadMany(anyString(), any())).thenReturn(userDescribes);
        doNothing().when(userDescribeDao).delete(any(UserDescribe.class));

        when(userDescribes.iterator()).thenReturn(userDescribeIterator);

        when(userDescribeIterator.hasNext()).thenReturn(true, false);
        when(userDescribeIterator.next()).thenReturn(userDescribe);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_USER_LIST;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(userServiceDao).delete(any(UserService.class));
        verify(userOrderDao).delete(any(UserOrder.class));
        verify(userDescribeDao).delete(any(UserDescribe.class));
        verify(userDao).delete(any(User.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameter2() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(0);

        when(userDao.read(1L)).thenReturn(null);

        String expected = Path.MOVE_BY_REDIRECT_TO_ADMINISTRATION_USER_LIST;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);
    }

    @Test (expected = ParameterException.class)
    public void executeAuthorizedAdminWithBadParameter() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("WRONG");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(0);

        object.execute(request, response);
    }

    @Test (expected = CommandAccessException.class)
    public void executeAuthorizedCustomerWithRightParameter() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getRoleId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test (expected = CommandAccessException.class)
    public void executeUnauthorizedWithRightParameter() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("1");

        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

    @Test (expected = ParameterException.class)
    public void executeWithBadParameter1() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn("");

        object.execute(request, response);
    }

    @Test (expected = ParameterException.class)
    public void executeWithBadParameter2() throws AppException, IOException, ServletException {
        when(request.getParameter("id")).thenReturn(null);

        object.execute(request, response);
    }

}