package ua.nure.veres.summaryTask.web.command.authentication;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.command.authentication.LogoutException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LogoutCommandTest {

    @InjectMocks
    private LogoutCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private User user;

    @Before
    public void init() {
        when(request.getSession(false)).thenReturn(session);
    }


    @Test
    public void executeSessionHasUser() throws LogoutException {
        when(session.getAttribute("user")).thenReturn(user);

        assertEquals(Path.COMMAND_REDIRECT_TO_LOGIN_PAGE, object.execute(request, response));

        verify(session).invalidate();
    }

    @Test(expected = LogoutException.class)
    public void executeSessionHasNoUser() throws LogoutException {
        when(session.getAttribute("user")).thenReturn(null);
        object.execute(request, response);
    }

}