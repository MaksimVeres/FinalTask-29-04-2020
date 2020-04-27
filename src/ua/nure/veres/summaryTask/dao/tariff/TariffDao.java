package ua.nure.veres.summaryTask.dao.tariff;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.db.DBManager;
import ua.nure.veres.summaryTask.db.Fields;
import ua.nure.veres.summaryTask.db.entity.Tariff;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.Messages;
import ua.nure.veres.summaryTask.exception.db.DBException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Tariff DAO.
 *
 * @see ua.nure.veres.summaryTask.dao.Dao
 */
class TariffDao implements Dao<Tariff, Long> {

    //CREATE
    private static final String SQL_CREATE_TARIFF = "INSERT INTO tariffs VALUES (DEFAULT, ?, ?, ?, ?, ?)";

    //READ
    private static final String SQL_READ_TARIFF = "SELECT * FROM tariffs WHERE id = ?";

    private static final String SQL_READ_TARIFF_BY_NAME = "SELECT * FROM tariffs WHERE name = ?";

    private static final String SQL_READ_ALL_TARIFFS = "SELECT * FROM tariffs";

    private static final String SQL_READ_MANY_TARIFFS_BY_SERVICE_ID = "SELECT * FROM tariffs WHERE service_id = ?";

    private static final String SQL_READ_MANY_TARIFFS_BY_NAME = "SELECT * FROM tariffs WHERE name = ?";

    //UPDATE
    private static final String SQL_UPDATE_TARIFF
            = "UPDATE tariffs SET name = ?, connection_payment = ?, month_payment = ?, feature = ?, service_id = ?" +
            " WHERE id = ?";

    private static final String SQL_UPDATE_TARIFF_NAME = "UPDATE tariffs SET name = ? WHERE id = ?";

    private static final String SQL_UPDATE_TARIFF_CONNECTION_PAYMENT
            = "UPDATE tariffs SET connection_payment = ? WHERE id = ?";

    private static final String SQL_UPDATE_TARIFF_MONTH_PAYMENT
            = "UPDATE tariffs SET month_payment = ? WHERE id = ?";

    private static final String SQL_UPDATE_TARIFF_FEATURE
            = "UPDATE tariffs SET feature = ? WHERE id = ?";

    private static final String SQL_UPDATE_TARIFF_SERVICE_ID
            = "UPDATE tariffs SET service_id = ? WHERE id = ?";

    //DELETE
    private static final String SQL_DELETE_TARIFF
            = "DELETE FROM tariffs WHERE id = ?";

    private static final Map<String, String> queryMap = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(TariffDao.class);

    private static TariffDao instance;

    static {
        BasicConfigurator.configure();

        //CREATE
        queryMap.put("CREATE", SQL_CREATE_TARIFF);

        //READ ONE
        queryMap.put("READ ONE id", SQL_READ_TARIFF);
        queryMap.put("READ ONE name", SQL_READ_TARIFF_BY_NAME);

        //READ MANY
        queryMap.put("READ MANY all", SQL_READ_ALL_TARIFFS);
        queryMap.put("READ MANY serviceId", SQL_READ_MANY_TARIFFS_BY_SERVICE_ID);
        queryMap.put("READ MANY name", SQL_READ_MANY_TARIFFS_BY_NAME);

        //UPDATE
        queryMap.put("UPDATE", SQL_UPDATE_TARIFF);
        queryMap.put("UPDATE name", SQL_UPDATE_TARIFF_NAME);
        queryMap.put("UPDATE connectionPayment", SQL_UPDATE_TARIFF_CONNECTION_PAYMENT);
        queryMap.put("UPDATE monthPayment", SQL_UPDATE_TARIFF_MONTH_PAYMENT);
        queryMap.put("UPDATE feature", SQL_UPDATE_TARIFF_FEATURE);
        queryMap.put("UPDATE serviceId", SQL_UPDATE_TARIFF_SERVICE_ID);

        //DELETE
        queryMap.put("DELETE", SQL_DELETE_TARIFF);

        LOG.debug("TariffDao queryMap was successfully initialized");
        LOG.trace("Number of TariffDao queries --> " + queryMap.size());
    }

    private TariffDao() {
        //no op
    }

    /**
     * Extracts a tariff entity from the result set.
     *
     * @param resultSet Result set from which a tariff entity will be extracted.
     * @return Tariff entity
     */
    private Tariff extractTariff(ResultSet resultSet) throws SQLException {
        Tariff tariff = new Tariff();
        tariff.setId(resultSet.getLong(Fields.ENTITY_ID));
        tariff.setName(resultSet.getString(Fields.TARIFF_NAME));
        tariff.setMonthPayment(resultSet.getDouble(Fields.TARIFF_MONTH_PAYMENT));
        tariff.setConnectionPayment(resultSet.getDouble(Fields.TARIFF_CONNECTION_PAYMENT));
        tariff.setFeature(resultSet.getString(Fields.TARIFF_FEATURE));
        tariff.setServiceId(resultSet.getLong(Fields.TARIFF_SERVICE_ID));
        return tariff;
    }

    /**
     * Fills prepared statement with Tariff parameters.
     *
     * @param preparedStatement prepared statement need to be filled
     * @param object            Tariff object
     * @param appendId          Append id to end or not
     */
    private void fillPreparedStatement(PreparedStatement preparedStatement, Tariff object, boolean appendId)
            throws SQLException {
        preparedStatement.setString(1, object.getName());
        preparedStatement.setDouble(2, object.getConnectionPayment());
        preparedStatement.setDouble(3, object.getMonthPayment());
        preparedStatement.setString(4, object.getFeature());
        preparedStatement.setLong(5, object.getServiceId());
        if (appendId) {
            preparedStatement.setLong(6, object.getId());
        }
    }

    @Override
    public void create(Tariff object) throws AppException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = dbManager.getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery("CREATE")
            );
            fillPreparedStatement(preparedStatement, object, false);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_CREATE_TARIFF, ex);
            throw new DBException(Messages.ERR_CANNOT_CREATE_TARIFF, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public Tariff read(Long id) throws AppException {
        Tariff tariff = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = dbManager.getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery("READ ONE id")
            );
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tariff = extractTariff(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_TARIFF, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_TARIFF, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return tariff;
    }

    @Override
    public List<Tariff> read() throws AppException {
        List<Tariff> tariffs = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = dbManager.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(
                    getQuery("READ MANY all")
            );
            while (resultSet.next()) {
                tariffs.add(extractTariff(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_TARIFFS, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_TARIFFS, ex);
        } finally {
            dbManager.close(connection, statement, resultSet);
        }
        return tariffs;
    }

    @Override
    public Tariff customReadOne(String daoQuery, Object... params) throws AppException {
        Tariff tariff = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = dbManager.getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery(daoQuery)
            );
            for (int i = 0; i < params.length; ++i) {
                preparedStatement.setObject((i + 1), params[i]);
            }
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tariff = extractTariff(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_TARIFF, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_TARIFF, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return tariff;
    }

    @Override
    public List<Tariff> customReadMany(String daoQuery, Object... params) throws AppException {
        List<Tariff> services = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = dbManager.getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery(daoQuery)
            );
            for (int i = 0; i < params.length; ++i) {
                preparedStatement.setObject((i + 1), params[i]);
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                services.add(extractTariff(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_TARIFFS, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_TARIFFS, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return services;
    }

    @Override
    public void update(Tariff object) throws AppException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = dbManager.getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery("UPDATE")
            );
            fillPreparedStatement(preparedStatement, object, true);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_UPDATE_TARIFF, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_TARIFF, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void customUpdate(Tariff object, String daoQuery, Object... params) throws AppException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = dbManager.getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery(daoQuery)
            );
            for (int i = 0; i < params.length; ++i) {
                preparedStatement.setObject((i + 1), params[i]);
            }
            preparedStatement.setLong(params.length, object.getId());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_UPDATE_TARIFF, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_TARIFF, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void delete(Tariff object) throws AppException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = dbManager.getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery("DELETE")
            );
            preparedStatement.setLong(1, object.getId());
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_DELETE_TARIFF, ex);
            throw new DBException(Messages.ERR_CANNOT_DELETE_TARIFF, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public String getQuery(String daoQuery) {
        return queryMap.get(daoQuery);
    }

    public static synchronized TariffDao getInstance() {
        if (instance == null) {
            instance = new TariffDao();
        }
        return instance;
    }

}
