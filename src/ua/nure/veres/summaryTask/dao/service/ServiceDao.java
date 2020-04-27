package ua.nure.veres.summaryTask.dao.service;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.db.DBManager;
import ua.nure.veres.summaryTask.db.Fields;
import ua.nure.veres.summaryTask.db.entity.Service;
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
 * Service DAO.
 *
 * @see ua.nure.veres.summaryTask.dao.Dao
 */
class ServiceDao implements Dao<Service, Long> {

    //CREATE
    private static final String SQL_CREATE_SERVICE = "INSERT INTO services VALUES (DEFAULT, ?)";

    //READ
    private static final String SQL_READ_SERVICE = "SELECT * FROM services WHERE id = ?";

    private static final String SQL_READ_SERVICE_BY_NAME = "SELECT * FROM services WHERE name = ?";

    private static final String SQL_READ_ALL_SERVICES = "SELECT * FROM services";

    //UPDATE
    private static final String SQL_UPDATE_SERVICE_NAME = "UPDATE services SET name = ? WHERE id = ?";

    //DELETE
    private static final String SQL_DELETE_SERVICE = "DELETE FROM services WHERE id = ?";

    private static final Map<String, String> queryMap = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(ServiceDao.class);

    private static ServiceDao instance;

    static {
        BasicConfigurator.configure();

        //CREATE
        queryMap.put("CREATE", SQL_CREATE_SERVICE);

        //READ ONE
        queryMap.put("READ ONE id", SQL_READ_SERVICE);
        queryMap.put("READ ONE name", SQL_READ_SERVICE_BY_NAME);

        //READ MANY
        queryMap.put("READ MANY all", SQL_READ_ALL_SERVICES);

        //UPDATE
        queryMap.put("UPDATE", SQL_UPDATE_SERVICE_NAME);
        queryMap.put("UPDATE name", SQL_UPDATE_SERVICE_NAME);

        //DELETE
        queryMap.put("DELETE", SQL_DELETE_SERVICE);

        LOG.debug("ServiceDao queryMap was successfully initialized");
        LOG.trace("Number of ServiceDao queries --> " + queryMap.size());
    }

    private ServiceDao() {
        //no op
    }

    public Connection getConnection() throws DBException {
        return DBManager.getInstance().getConnection();
    }

    /**
     * Extracts a service entity from the result set.
     *
     * @param resultSet Result set from which a service entity will be extracted.
     * @return Service entity.
     */
    private Service extractService(ResultSet resultSet) throws SQLException {
        Service service = new Service();
        service.setId(resultSet.getLong(Fields.ENTITY_ID));
        service.setName(resultSet.getString(Fields.SERVICE_NAME));
        return service;
    }

    @Override
    public void create(Service object) throws AppException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery("CREATE")
            );
            preparedStatement.setString(1, object.getName());
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_CREATE_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_CREATE_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public Service read(Long id) throws AppException {
        Service service = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery("READ ONE id")
            );
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                service = extractService(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return service;
    }

    @Override
    public List<Service> read() throws AppException {
        List<Service> services = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(getQuery("READ MANY all"));
            while (resultSet.next()) {
                services.add(extractService(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_SERVICES, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_SERVICES, ex);
        } finally {
            dbManager.close(connection, statement, resultSet);
        }
        return services;
    }

    @Override
    public Service customReadOne(String daoQuery, Object... params) throws AppException {
        Service service = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(getQuery(daoQuery));
            for (int i = 0; i < params.length; ++i) {
                preparedStatement.setObject((i + 1), params[i]);
            }
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                service = extractService(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return service;
    }

    @Override
    public List<Service> customReadMany(String daoQuery, Object... params) throws AppException {
        List<Service> services = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery(daoQuery)
            );
            for (int i = 0; i < params.length; ++i) {
                preparedStatement.setObject((i + 1), params[i]);
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                services.add(extractService(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_SERVICES, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_SERVICES, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return services;
    }

    @Override
    public void update(Service object) throws AppException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery("UPDATE")
            );
            preparedStatement.setString(1, object.getName());
            preparedStatement.setLong(2, object.getId());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_UPDATE_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void customUpdate(Service object, String daoQuery, Object... params) throws AppException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery(daoQuery)
            );
            for (int i = 0; i < params.length; ++i) {
                preparedStatement.setObject((i + 1), params[i]);
            }
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_UPDATE_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void delete(Service object) throws AppException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        DBManager dbManager = DBManager.getInstance();

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(
                    getQuery("DELETE")
            );
            preparedStatement.setLong(1, object.getId());
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_DELETE_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_DELETE_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public String getQuery(final String daoQuery) {
        return queryMap.get(daoQuery);
    }

    public static synchronized ServiceDao getInstance() {
        if (instance == null) {
            instance = new ServiceDao();
        }
        return instance;
    }

}
