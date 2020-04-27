package ua.nure.veres.summaryTask.dao.user;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.db.DBManager;
import ua.nure.veres.summaryTask.db.Fields;
import ua.nure.veres.summaryTask.db.entity.User;
import ua.nure.veres.summaryTask.exception.AppException;
import ua.nure.veres.summaryTask.exception.Messages;
import ua.nure.veres.summaryTask.exception.db.DBException;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * User DAO.
 *
 * @see ua.nure.veres.summaryTask.dao.Dao
 */
class UserDao implements Dao<User, Long> {

    //CREATE
    private static final String SQL_CREATE_USER = "INSERT INTO users VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?)";

    //READ
    private static final String SQL_READ_USER = "SELECT * FROM users WHERE id = ?";

    private static final String SQL_READ_USER_BY_LOGIN = "SELECT * FROM users WHERE login = ?";

    private static final String SQL_READ_ALL_USERS = "SELECT * FROM users";

    //UPDATE
    private static final String SQL_UPDATE_USER = "UPDATE users SET login = ?, password = ?, first_name = ?," +
            " last_name = ?, role_id = ?, status_id = ?, account_state = ? WHERE id = ?";

    private static final String SQL_UPDATE_USER_LOGIN = "UPDATE users SET login = ? WHERE id = ?";

    private static final String SQL_UPDATE_USER_PASSWORD = "UPDATE users SET password = ? WHERE id = ?";

    private static final String SQL_UPDATE_USER_FIRST_NAME = "UPDATE users SET first_name = ? WHERE id = ?";

    private static final String SQL_UPDATE_USER_LAST_NAME = "UPDATE users SET last_name = ? WHERE id = ?";

    private static final String SQL_UPDATE_USER_ROLE_ID = "UPDATE users SET role_id = ? WHERE id = ?";

    private static final String SQL_UPDATE_USER_STATUS_ID = "UPDATE users SET status_id = ? WHERE id = ?";

    private static final String SQL_UPDATE_USER_ACCOUNT_STATE = "UPDATE users SET account_state = ? WHERE id = ?";

    //DELETE
    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE id = ?";

    private static final Map<String, String> queryMap = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(UserDao.class);

    private static UserDao instance;

    static {
        BasicConfigurator.configure();

        //CREATE
        queryMap.put("CREATE", SQL_CREATE_USER);

        //READ ONE
        queryMap.put("READ ONE id", SQL_READ_USER);
        queryMap.put("READ ONE login", SQL_READ_USER_BY_LOGIN);

        //READ MANY
        queryMap.put("READ MANY all", SQL_READ_ALL_USERS);

        //UPDATE
        queryMap.put("UPDATE", SQL_UPDATE_USER);
        queryMap.put("UPDATE login", SQL_UPDATE_USER_LOGIN);
        queryMap.put("UPDATE password", SQL_UPDATE_USER_PASSWORD);
        queryMap.put("UPDATE firstName", SQL_UPDATE_USER_FIRST_NAME);
        queryMap.put("UPDATE lastName", SQL_UPDATE_USER_LAST_NAME);
        queryMap.put("UPDATE roleId", SQL_UPDATE_USER_ROLE_ID);
        queryMap.put("UPDATE statusId", SQL_UPDATE_USER_STATUS_ID);
        queryMap.put("UPDATE accountState", SQL_UPDATE_USER_ACCOUNT_STATE);

        //DELETE
        queryMap.put("DELETE", SQL_DELETE_USER);

        LOG.debug("UserDao queryMap was successfully initialized");
        LOG.trace("Number of UserDao queries --> " + queryMap.size());
    }

    private UserDao() {
        //no op
    }

    /**
     * Extracts a user entity from the result set.
     *
     * @param resultSet Result set from which a user entity will be extracted.
     * @return User entity
     */
    private User extractUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong(Fields.ENTITY_ID));
        user.setLogin(resultSet.getString(Fields.USER_LOGIN));
        user.setPassword(resultSet.getString(Fields.USER_PASSWORD));
        user.setFirstName(resultSet.getString(Fields.USER_FIRST_NAME));
        user.setLastName(resultSet.getString(Fields.USER_LAST_NAME));
        user.setRoleId(resultSet.getInt(Fields.USER_ROLE_ID));
        user.setStatusId(resultSet.getInt(Fields.USER_STATUS_ID));
        user.setAccountState(resultSet.getDouble(Fields.USER_ACCOUNT_STATE));
        return user;
    }

    /**
     * Fills prepared statement with Tariff parameters.
     *
     * @param preparedStatement prepared statement need to be filled
     * @param object            Tariff object
     */
    private void fillPreparedStatement(PreparedStatement preparedStatement, User object, boolean appendId)
            throws SQLException {
        preparedStatement.setString(1, object.getLogin());
        preparedStatement.setString(2, object.getPassword());
        preparedStatement.setString(3, object.getFirstName());
        preparedStatement.setString(4, object.getLastName());
        preparedStatement.setInt(5, object.getRoleId());
        preparedStatement.setInt(6, object.getStatusId());
        preparedStatement.setDouble(7, object.getAccountState());
        if (appendId) {
            preparedStatement.setLong(8, object.getId());
        }
    }

    @Override
    public void create(User object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_CREATE_USER, ex);
            throw new DBException(Messages.ERR_CANNOT_CREATE_USER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public User read(Long id) throws AppException {
        User user = null;
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
                user = extractUser(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_USER, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_USER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return user;
    }

    @Override
    public List<User> read() throws AppException {
        List<User> users = new ArrayList<>();
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
                users.add(extractUser(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_USERS, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_USERS, ex);
        } finally {
            dbManager.close(connection, statement, resultSet);
        }
        return users;
    }

    @Override
    public User customReadOne(String daoQuery, Object... params) throws AppException {
        User user = null;
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
                user = extractUser(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_USER, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_USER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return user;
    }

    @Override
    public List<User> customReadMany(String daoQuery, Object... params) throws AppException {
        List<User> users = new ArrayList<>();
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
                users.add(extractUser(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_USERS, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_USERS, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return users;
    }

    @Override
    public void update(User object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_UPDATE_USER, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_USER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void customUpdate(User object, String daoQuery, Object... params) throws AppException {
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
            preparedStatement.setLong(params.length + 1, object.getId());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_UPDATE_USER, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_USER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void delete(User object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_DELETE_USER, ex);
            throw new DBException(Messages.ERR_CANNOT_DELETE_USER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public String getQuery(String daoQuery) {
        return queryMap.get(daoQuery);
    }

    public static synchronized UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }
}
