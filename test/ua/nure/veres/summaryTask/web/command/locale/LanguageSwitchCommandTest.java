package ua.nure.veres.summaryTask.web.command.locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LanguageSwitchCommandTest {

    @InjectMocks
    private LanguageSwitchCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Before
    public void init() {
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void executeWithRightParameters() throws AppException {
        when(request.getParameter("lang")).thenReturn("ru");

        doNothing().when(session).setAttribute(anyString(), any());

        String expected = Path.MOVE_BY_REDIRECT_TO_MAIN_PAGE;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(session).setAttribute(anyString(), any());
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter1() throws AppException {
        when(request.getParameter("lang")).thenReturn("");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter2() throws AppException {
        when(request.getParameter("lang")).thenReturn(null);

        object.execute(request, response);
    }

}