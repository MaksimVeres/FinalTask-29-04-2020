package ua.nure.veres.summaryTask.web.command.change.user;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.access.CommandAccessException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.user.UserUpdateException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChangeFullNameCommandTest {

    @InjectMocks
    private ChangeFullNameCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private DaoFactory userDaoFactory;

    @Mock
    private Dao userDao;

    @Mock
    private User sessionUser;

    @Mock
    private User dbUser;

    @Before
    @SuppressWarnings("unchecked")
    public void init()  {
        when(request.getSession()).thenReturn(session);
        when(userDaoFactory.getDao()).thenReturn(userDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeAuthorizedWithRightParameters() throws AppException {
        when(request.getParameter("firstName")).thenReturn("testFirstName");
        when(request.getParameter("lastName")).thenReturn("testLastName");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getLogin()).thenReturn("testLogin");

        when(userDao.customReadOne("READ ONE login", "testLogin")).thenReturn(dbUser);
        doNothing().when(userDao).update(any(User.class));

        when(dbUser.getLogin()).thenReturn("testLogin");

        String expected = Path.MOVE_BY_REDIRECT_TO_PERSONAL_CABINET_PAGE;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(dbUser).setFirstName("testFirstName");
        verify(dbUser).setLastName("testLastName");
    }

    @Test(expected = UserUpdateException.class)
    public void executeAuthorizedWithWrongParameters() throws AppException {
        when(request.getParameter("firstName")).thenReturn("testFirstName");
        when(request.getParameter("lastName")).thenReturn("testLastName");

        when(session.getAttribute("user")).thenReturn(sessionUser);

        when(sessionUser.getLogin()).thenReturn("testLogin");

        when(userDao.customReadOne("READ ONE login", "testLogin")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = CommandAccessException.class)
    public void executeUnauthorizedWithRightParameters() throws AppException {
        when(request.getParameter("firstName")).thenReturn("testFirstName");
        when(request.getParameter("lastName")).thenReturn("testLastName");

        when(session.getAttribute("user")).thenReturn(null);

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter1() throws AppException {
        when(request.getParameter("firstName")).thenReturn(null);
        when(request.getParameter("lastName")).thenReturn("testLastName");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter2() throws AppException {
        when(request.getParameter("firstName")).thenReturn("testFirstName");
        when(request.getParameter("lastName")).thenReturn(null);

        object.execute(request, response);
    }
}