package ua.nure.veres.summaryTask.dao.userDescribe;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.db.DBManager;
import ua.nure.veres.summaryTask.db.Fields;
import ua.nure.veres.summaryTask.db.entity.UserDescribe;
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
 * UserDescribe DAO.
 *
 * @see ua.nure.veres.summaryTask.dao.Dao
 */
class UserDescribeDao implements Dao<UserDescribe, Long> {

    //CREATE
    private static final String SQL_CREATE_USER_DESCRIBE = "INSERT INTO user_describes VALUES (DEFAULT, ?, ?, ?, ?)";

    //READ
    private static final String SQL_READ_USER_DESCRIBE = "SELECT * FROM user_describes WHERE id = ?";

    private static final String SQL_READ_ALL_USER_DESCRIBES = "SELECT * FROM user_describes";

    private static final String SQL_READ_MANY_USER_DESCRIBES_BY_USER_LOGIN = SQL_READ_ALL_USER_DESCRIBES +
            " INNER JOIN user_services" +
            " ON user_describes.user_service_id = user_services.id " +
            "WHERE user_services.user_id IN (SELECT users.id FROM users WHERE users.login = ?)";

    private static final String SQL_READ_MANY_USER_DESCRIBES_BY_TARIFF_ID = SQL_READ_ALL_USER_DESCRIBES +
            " INNER JOIN user_services" +
            " ON user_describes.user_service_id = user_services.id " +
            "WHERE user_services.tariff_id = ?";

    //UPDATE
    private static final String SQL_UPDATE_USER_DESCRIBE = "UPDATE user_describes SET user_service_id = ?" +
            ", user_phone = ?, comment = ?, describe_date = ? WHERE id = ?";

    //DELETE
    private static final String SQL_DELETE_USER_DESCRIBE = "DELETE FROM user_describes WHERE id = ?";

    private static final Map<String, String> queryMap = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(UserDescribeDao.class);

    private static UserDescribeDao instance;

    static {
        BasicConfigurator.configure();

        //CREATE
        queryMap.put("CREATE", SQL_CREATE_USER_DESCRIBE);

        //READ ONE
        queryMap.put("READ ONE id", SQL_READ_USER_DESCRIBE);

        //READ MANY
        queryMap.put("READ MANY all", SQL_READ_ALL_USER_DESCRIBES);
        queryMap.put("READ MANY User.login", SQL_READ_MANY_USER_DESCRIBES_BY_USER_LOGIN);
        queryMap.put("READ MANY Tariff.id", SQL_READ_MANY_USER_DESCRIBES_BY_TARIFF_ID);

        //UPDATE
        queryMap.put("UPDATE", SQL_UPDATE_USER_DESCRIBE);

        //DELETE
        queryMap.put("DELETE", SQL_DELETE_USER_DESCRIBE);

        LOG.debug("UserDescribeDao queryMap was successfully initialized");
        LOG.trace("Number of UserDescribeDao queries --> " + queryMap.size());
    }

    private UserDescribeDao() {
        //no op
    }

    /**
     * Extracts an user describe entity from the result set.
     *
     * @param resultSet Result set from which a user describe entity will be extracted.
     * @return UserDescribe entity.
     */
    private UserDescribe extractUserDescribe(ResultSet resultSet) throws SQLException {
        UserDescribe userDescribe = new UserDescribe();
        userDescribe.setId(resultSet.getLong(Fields.ENTITY_ID));
        userDescribe.setUserServiceId(resultSet.getLong(Fields.USER_DESCRIBE_USER_SERVICE_ID));
        userDescribe.setUserPhone(resultSet.getString(Fields.USER_DESCRIBE_USER_PHONE));
        userDescribe.setComment(resultSet.getString(Fields.USER_DESCRIBE_COMMENT));
        userDescribe.setDescribeDate(resultSet.getDate(Fields.USER_DESCRIBE_DESCRIBE_DATE));
        return userDescribe;
    }

    /**
     * Fills prepared statement with UserDescribe parameters.
     *
     * @param preparedStatement prepared statement need to be filled
     * @param object            UserDescribe object
     * @param appendId          Append id to end or not
     */
    private void fillPreparedStatement(PreparedStatement preparedStatement, UserDescribe object, boolean appendId)
            throws SQLException {
        preparedStatement.setLong(1, object.getUserServiceId());
        preparedStatement.setString(2, object.getUserPhone());
        preparedStatement.setString(3, object.getComment());
        preparedStatement.setDate(4, object.getDescribeDate());
        if (appendId) {
            preparedStatement.setLong(5, object.getId());
        }
    }

    @Override
    public void create(UserDescribe object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_CREATE_USER_DESCRIBE, ex);
            throw new DBException(Messages.ERR_CANNOT_CREATE_USER_DESCRIBE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public UserDescribe read(Long id) throws AppException {
        UserDescribe userDescribe = null;
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
                userDescribe = extractUserDescribe(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_USER_DESCRIBE, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_USER_DESCRIBE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return userDescribe;
    }

    @Override
    public List<UserDescribe> read() throws AppException {
        List<UserDescribe> userDescribes = new ArrayList<>();
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
                userDescribes.add(extractUserDescribe(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_USER_DESCRIBES, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_USER_DESCRIBES, ex);
        } finally {
            dbManager.close(connection, statement, resultSet);
        }
        return userDescribes;
    }

    @Override
    public UserDescribe customReadOne(String daoQuery, Object... params) throws AppException {
        UserDescribe userDescribe = null;
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
                userDescribe = extractUserDescribe(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_USER_DESCRIBE, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_USER_DESCRIBE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return userDescribe;
    }

    @Override
    public List<UserDescribe> customReadMany(String daoQuery, Object... params) throws AppException {
        List<UserDescribe> userDescribes = new ArrayList<>();
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
                userDescribes.add(extractUserDescribe(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_USER_DESCRIBES, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_USER_DESCRIBES, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return userDescribes;
    }

    @Override
    public void update(UserDescribe object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_UPDATE_USER_DESCRIBE, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_USER_DESCRIBE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void customUpdate(UserDescribe object, String daoQuery, Object... params) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_UPDATE_USER_DESCRIBE, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_USER_DESCRIBE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void delete(UserDescribe object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_DELETE_USER_DESCRIBE, ex);
            throw new DBException(Messages.ERR_CANNOT_DELETE_USER_DESCRIBE, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public String getQuery(String daoQuery) {
        return queryMap.get(daoQuery);
    }

    public static synchronized UserDescribeDao getInstance() {
        if (instance == null) {
            instance = new UserDescribeDao();
        }
        return instance;
    }
}
