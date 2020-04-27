package ua.nure.veres.summaryTask.dao.userService;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.db.DBManager;
import ua.nure.veres.summaryTask.db.Fields;
import ua.nure.veres.summaryTask.db.entity.UserService;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.Messages;
import ua.nure.veres.summaryTask.exception.db.DBException;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * UserService DAO.
 *
 * @see ua.nure.veres.summaryTask.dao.Dao
 */
class UserServiceDao implements Dao<UserService, Long> {

    //CREATE
    private static final String SQL_CREATE_USER_SERVICE = "INSERT INTO user_services VALUES (DEFAULT, ?, ?, ?, ?)";

    //READ
    private static final String SQL_READ_USER_SERVICE = "SELECT * FROM user_services WHERE id = ?";

    private static final String SQL_READ_MANY_USER_SERVICES_BY_USER_ID
            = "SELECT * FROM user_services WHERE user_id = ?";

    private static final String SQL_READ_MANY_USER_SERVICES_BY_USER_LOGIN = "SELECT * FROM user_services " +
            "INNER JOIN users ON user_id = users.id AND users.login = ?";

    private static final String SQL_READ_MANY_USER_SERVICES_BY_TARIFF_ID
            = "SELECT * FROM user_services WHERE tariff_id = ?";

    private static final String SQL_READ_ALL_USER_SERVICES = "SELECT * FROM user_services";

    //UPDATE
    private static final String SQL_UPDATE_USER_SERVICE = "UPDATE user_services SET user_id = ?, tariff_id = ?" +
            ", address = ?, last_payment_date = ? WHERE id = ?";

    //DELETE
    private static final String SQL_DELETE_USER_SERVICE = "DELETE FROM user_services WHERE id = ?";

    private static final Map<String, String> queryMap = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(UserServiceDao.class);

    private static UserServiceDao instance;

    static {
        BasicConfigurator.configure();

        //CREATE
        queryMap.put("CREATE", SQL_CREATE_USER_SERVICE);

        //READ ONE
        queryMap.put("READ ONE id", SQL_READ_USER_SERVICE);

        //READ MANY
        queryMap.put("READ MANY all", SQL_READ_ALL_USER_SERVICES);
        queryMap.put("READ MANY userId", SQL_READ_MANY_USER_SERVICES_BY_USER_ID);
        queryMap.put("READ MANY User.login", SQL_READ_MANY_USER_SERVICES_BY_USER_LOGIN);
        queryMap.put("READ MANY tariffId", SQL_READ_MANY_USER_SERVICES_BY_TARIFF_ID);

        //UPDATE
        queryMap.put("UPDATE", SQL_UPDATE_USER_SERVICE);

        //DELETE
        queryMap.put("DELETE", SQL_DELETE_USER_SERVICE);

        LOG.debug("UserServiceDao queryMap was successfully initialized");
        LOG.trace("Number of UserServiceDao queries --> " + queryMap.size());
    }

    private UserServiceDao() {
        //no op
    }

    /**
     * Extracts UserService entity from the result set.
     *
     * @param resultSet Result set from which a UserService entity will be extracted.
     * @return UserService entity
     */
    private UserService extractUserService(ResultSet resultSet) throws SQLException {
        UserService userService = new UserService();
        userService.setId(resultSet.getLong(Fields.ENTITY_ID));
        userService.setUserId(resultSet.getLong(Fields.USER_SERVICE_USER_ID));
        userService.setTariffId(resultSet.getLong(Fields.USER_SERVICE_TARIFF_ID));
        userService.setLastPaymentDate(resultSet.getDate(Fields.USER_SERVICE_LAST_PAYMENT_DATE));
        userService.setAddress(resultSet.getString(Fields.USER_SERVICE_ADDRESS));
        return userService;
    }

    /**
     * Fills prepared statement with UserService parameters.
     *
     * @param preparedStatement prepared statement need to be filled
     * @param object            UserService object
     * @param appendId          Append id to end or not
     */
    private void fillPreparedStatement(PreparedStatement preparedStatement, UserService object, boolean appendId)
            throws SQLException {
        preparedStatement.setLong(1, object.getUserId());
        preparedStatement.setLong(2, object.getTariffId());
        preparedStatement.setString(3, object.getAddress());
        preparedStatement.setDate(4, new java.sql.Date(object.getLastPaymentDate().getTime()));
        if (appendId) {
            preparedStatement.setLong(5, object.getId());
        }
    }

    @Override
    public void create(UserService object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_CREATE_USER_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_CREATE_USER_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public UserService read(Long id) throws AppException {
        UserService userService = null;
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
                userService = extractUserService(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_USER_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_USER_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return userService;
    }

    @Override
    public List<UserService> read() throws AppException {
        List<UserService> userServices = new ArrayList<>();
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
                userServices.add(extractUserService(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_USER_SERVICES, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_USER_SERVICES, ex);
        } finally {
            dbManager.close(connection, statement, resultSet);
        }
        return userServices;
    }

    @Override
    public UserService customReadOne(String daoQuery, Object... params) throws AppException {
        UserService userService = null;
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
                userService = extractUserService(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_USER_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_USER_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return userService;
    }

    @Override
    public List<UserService> customReadMany(String daoQuery, Object... params) throws AppException {
        List<UserService> userServices = new ArrayList<>();
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
                userServices.add(extractUserService(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_USER_SERVICES, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_USER_SERVICES, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return userServices;
    }

    @Override
    public void update(UserService object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_UPDATE_USER_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_USER_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void customUpdate(UserService object, String daoQuery, Object... params) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_UPDATE_USER_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_USER_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void delete(UserService object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_DELETE_USER_SERVICE, ex);
            throw new DBException(Messages.ERR_CANNOT_DELETE_USER_SERVICE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public String getQuery(String daoQuery) {
        return queryMap.get(daoQuery);
    }

    public static synchronized UserServiceDao getInstance() {
        if (instance == null) {
            instance = new UserServiceDao();
        }
        return instance;
    }
}
