package ua.nure.veres.summaryTask.web.command.get.tariff;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.dao.service.ServiceDaoFactory;
import ua.nure.veres.summaryTask.dao.tariff.TariffDaoFactory;
import ua.nure.veres.summaryTask.db.entity.Service;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.command.value.ParameterException;
import ua.nure.veres.summaryTask.exception.download.DownloadException;
import ua.nure.veres.summaryTask.web.command.get.GetCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

/**
 * Get tariffs txt command.
 * Downloads tariffs list in .txt format.
 */
public class GetTariffsTxtCommand extends GetCommand {

    private static final long serialVersionUID = 1000000000000111L;

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private static final String PARAMETER_SERVICE_ID = "serviceId";

    private static final Logger LOG = Logger.getLogger(GetTariffsTxtCommand.class);

    static {
        BasicConfigurator.configure();
    }

    @Override
    protected String doGetCommand(HttpServletRequest request, HttpServletResponse response) throws AppException {
        LOG.debug("Command starts");

        String serviceId = getParameter(PARAMETER_SERVICE_ID, request);
        LOG.trace(String.format("Request parameter: %s --> %s", PARAMETER_SERVICE_ID, serviceId));

        if (!validateParameter(serviceId)) {
            throw new ParameterException(PARAMETER_SERVICE_ID);
        }

        long serviceIdValue;
        try {
            serviceIdValue = Long.parseLong(serviceId);
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParameterException(PARAMETER_SERVICE_ID);
        }

        response.setContentType("text/plain");
        response.setHeader("Content-disposition", "attachment; filename=tariffs.txt");

        DaoFactory daoFactory = new ServiceDaoFactory();
        Dao<Service, Long> serviceDao = daoFactory.getDao();
        Service service = serviceDao.read(serviceIdValue);
        LOG.trace("Found in DB: service --> " + service);

        if (service == null) {
            throw new DownloadException(DownloadException.MESSAGE_DOWNLOAD_ERROR_OCCURRED);
        }

        daoFactory = new TariffDaoFactory();
        Dao<Tariff, Long> tariffDao = daoFactory.getDao();
        List<Tariff> tariffs = tariffDao.customReadMany("READ MANY serviceId", serviceIdValue);
        LOG.trace("Found in DB: tariffs, size --> " + tariffs.size());

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(response.getOutputStream());

            writer.append("Tariff list for service#")
                    .append(service.getId().toString())
                    .append(" \"")
                    .append(service.getName())
                    .append("\"")
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR);

            for (Tariff tariff : tariffs) {
                writer.print(getTariffDescription(tariff));
            }
            writer.append("Date: ").append(LocalDate.now().toString());
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DownloadException(DownloadException.MESSAGE_DOWNLOAD_ERROR_OCCURRED);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }

        LOG.debug("Command finished");
        return COMMAND_EXECUTION_SUCCESS;
    }

    /**
     * Formats a tariff to description view.
     *
     * @param tariff Tariff entity
     * @return Tariff description
     */
    private String getTariffDescription(Tariff tariff) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Tariff #")
                .append(tariff.getId().toString())
                .append(LINE_SEPARATOR);
        stringBuilder.append("Name : ")
                .append(tariff.getName())
                .append(LINE_SEPARATOR);
        stringBuilder.append("Connection payment : ")
                .append(tariff.getConnectionPayment())
                .append(" UAH")
                .append(LINE_SEPARATOR);
        stringBuilder.append("Month payment : ")
                .append(tariff.getMonthPayment())
                .append(" UAH")
                .append(LINE_SEPARATOR);
        stringBuilder.append("Feature : ")
                .append(LINE_SEPARATOR);
        stringBuilder.append(tariff.getFeature())
                .append(LINE_SEPARATOR)
                .append("------------------------")
                .append(LINE_SEPARATOR);
        return stringBuilder.toString();
    }
}
