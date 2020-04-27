package ua.nure.veres.summaryTask.web.command.decline.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.db.entity.UserDescribe;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeclineDescribeRequestCommandTest {

    @InjectMocks
    private DeclineDescribeRequestCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private DaoFactory userDescribeDaoFactory;

    @Mock
    private Dao userDescribeDao;

    @Mock
    private UserDescribe userDescribe;

    @Mock
    private User user;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(request.getSession()).thenReturn(session);
        when(userDescribeDaoFactory.getDao()).thenReturn(userDescribeDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithRightParameters() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);
        when(session.getAttribute("replyDescribeRequest")).thenReturn("someReplyRequest");

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(userDescribeDao.read(1L)).thenReturn(userDescribe);
        doNothing().when(userDescribeDao).delete(any(UserDescribe.class));

        String expected = "someReplyRequest";
        String actually = object.execute(request, response);
        assertEquals(actually, expected);

        verify(userDescribeDao).delete(any(UserDescribe.class));
    }

    @Test(expected = ParameterException.class)
    @SuppressWarnings("unchecked")
    public void executeAuthorizedAdminWithWrongParameter1() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("1");

        when(userDescribeDao.read(1L)).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeAuthorizedAdminWithWrongParameter2() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(request.getParameter("userRequestId")).thenReturn("WRONG");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeAuthorizedAdminWithWrongParameter3() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeAuthorizedCustomer() throws AppException {
        when(session.getAttribute("user")).thenReturn(user);

        when(user.getRoleId()).thenReturn(1);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorized() throws AppException {
        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }


}