package ua.nure.veres.summaryTask.web.command.open;

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
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.web.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OpenTariffsCommandTest {

    @InjectMocks
    private OpenTariffsCommand object;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private TariffDaoFactory tariffDaoFactory;

    @Mock
    private ServiceDaoFactory serviceDaoFactory;

    @Mock
    private Dao tariffDao;

    @Mock
    private Dao serviceDao;

    @Mock
    private List<Tariff> tariffs;

    @Mock
    private Service service;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(tariffDaoFactory.getDao()).thenReturn(tariffDao);
        when(serviceDaoFactory.getDao()).thenReturn(serviceDao);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeWithRightParameters() throws AppException {
        when(request.getParameter("serviceId")).thenReturn("1");
        doNothing().when(request).setAttribute(anyString(), any());

        when(tariffDao.customReadMany(anyString(), any())).thenReturn(tariffs);

        when(serviceDao.read(1L)).thenReturn(service);

        String expected = Path.MOVE_BY_FORWARD_TO_TARIFF_LIST;
        String actually = object.execute(request, response);
        assertEquals(expected, actually);

        verify(request, times(2)).setAttribute(anyString(), any());
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter1() throws AppException {
        when(request.getParameter("serviceId")).thenReturn("WRONG");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter2() throws AppException {
        when(request.getParameter("serviceId")).thenReturn("");

        object.execute(request, response);
    }

    @Test(expected = ParameterException.class)
    public void executeWithBadParameter3() throws AppException {
        when(request.getParameter("serviceId")).thenReturn(null);

        object.execute(request, response);
    }

}