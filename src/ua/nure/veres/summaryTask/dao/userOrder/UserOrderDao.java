package ua.nure.veres.summaryTask.dao.userOrder;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.db.DBManager;
import ua.nure.veres.summaryTask.db.Fields;
import ua.nure.veres.summaryTask.db.entity.UserOrder;
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
 * UserOrder DAO.
 *
 * @see ua.nure.veres.summaryTask.dao.Dao
 */
class UserOrderDao implements Dao<UserOrder, Long> {

    //CREATE
    private static final String SQL_CREATE_USER_ORDER = "INSERT INTO user_orders VALUES (DEFAULT, ?, ?, ?, ?, ?)";

    //READ
    private static final String SQL_READ_USER_ORDER = "SELECT * FROM user_orders WHERE id = ?";

    private static final String SQL_READ_MANY_USER_ORDERS_BY_USER_ID
            = "SELECT * FROM user_orders WHERE user_id = ?";

    private static final String SQL_READ_MANY_USER_ORDERS_BY_USER_LOGIN = "SELECT * FROM user_orders JOIN users " +
            "ON user_orders.user_id = users.id AND users.login = ?";

    private static final String SQL_READ_MANY_USER_ORDERS_BY_TARIFF_ID
            = "SELECT * FROM user_orders WHERE tariff_id = ?";

    private static final String SQL_READ_ALL_USER_ORDERS = "SELECT * FROM user_orders";

    //UPDATE
    private static final String SQL_UPDATE_USER_ORDER = "UPDATE user_orders SET user_id = ?, tariff_id = ?," +
            " user_phone = ?, comment = ?, order_date = ? WHERE id = ?";

    //DELETE
    private static final String SQL_DELETE_USER_ORDER = "DELETE FROM user_orders WHERE id = ?";

    private static final Map<String, String> queryMap = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(UserOrderDao.class);

    private static UserOrderDao instance;

    static {
        BasicConfigurator.configure();

        //CREATE
        queryMap.put("CREATE", SQL_CREATE_USER_ORDER);

        //READ ONE
        queryMap.put("READ ONE id", SQL_READ_USER_ORDER);

        //READ MANY
        queryMap.put("READ MANY userId", SQL_READ_MANY_USER_ORDERS_BY_USER_ID);
        queryMap.put("READ MANY User.login", SQL_READ_MANY_USER_ORDERS_BY_USER_LOGIN);
        queryMap.put("READ MANY tariffId", SQL_READ_MANY_USER_ORDERS_BY_TARIFF_ID);
        queryMap.put("READ MANY all", SQL_READ_ALL_USER_ORDERS);

        //UPDATE
        queryMap.put("UPDATE", SQL_UPDATE_USER_ORDER);

        //DELETE
        queryMap.put("DELETE", SQL_DELETE_USER_ORDER);

        LOG.debug("UserOrderDao queryMap was successfully initialized");
        LOG.trace("Number of UserOrderDao queries --> " + queryMap.size());
    }

    private UserOrderDao() {
        //no op
    }

    /**
     * Extracts an user order entity from the result set.
     *
     * @param resultSet Result set from which a user order entity will be extracted.
     * @return UserOrder entity.
     */
    private UserOrder extractUserOrder(ResultSet resultSet) throws SQLException {
        UserOrder userOrder = new UserOrder();
        userOrder.setId(resultSet.getLong(Fields.ENTITY_ID));
        userOrder.setUserId(resultSet.getLong(Fields.USER_ORDER_USER_ID));
        userOrder.setTariffId(resultSet.getLong(Fields.USER_ORDER_TARIFF_ID));
        userOrder.setUserPhone(resultSet.getString(Fields.USER_ORDER_USER_PHONE));
        userOrder.setComment(resultSet.getString(Fields.USER_ORDER_COMMENT));
        userOrder.setOrderDate(resultSet.getDate(Fields.USER_ORDER_ORDER_DATE));
        return userOrder;
    }

    /**
     * Fills prepared statement with UserOrder parameters.
     *
     * @param preparedStatement prepared statement need to be filled
     * @param object            UserOrder object
     * @param appendId          Append id to end or not
     */
    private void fillPreparedStatement(PreparedStatement preparedStatement, UserOrder object, boolean appendId)
            throws SQLException {
        preparedStatement.setLong(1, object.getUserId());
        preparedStatement.setLong(2, object.getTariffId());
        preparedStatement.setString(3, object.getUserPhone());
        preparedStatement.setString(4, object.getComment());
        preparedStatement.setDate(5, object.getOrderDate());
        if (appendId) {
            preparedStatement.setLong(6, object.getId());
        }
    }

    @Override
    public void create(UserOrder object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_CREATE_USER_ORDER, ex);
            throw new DBException(Messages.ERR_CANNOT_CREATE_USER_ORDER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public UserOrder read(Long id) throws AppException {
        UserOrder userOrder = null;
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
                userOrder = extractUserOrder(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_USER_ORDER, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_USER_ORDER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return userOrder;
    }

    @Override
    public List<UserOrder> read() throws AppException {
        List<UserOrder> userOrders = new ArrayList<>();
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
                userOrders.add(extractUserOrder(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_USER_ORDERS, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_USER_ORDERS, ex);
        } finally {
            dbManager.close(connection, statement, resultSet);
        }
        return userOrders;
    }

    @Override
    public UserOrder customReadOne(String daoQuery, Object... params) throws AppException {
        UserOrder userOrder = null;
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
                userOrder = extractUserOrder(resultSet);
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_USER_ORDER, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_USER_ORDER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return userOrder;
    }

    @Override
    public List<UserOrder> customReadMany(String daoQuery, Object... params) throws AppException {
        List<UserOrder> userOrders = new ArrayList<>();
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
                userOrders.add(extractUserOrder(resultSet));
            }
            connection.commit();
        } catch (SQLException ex) {
            dbManager.rollback(connection);
            LOG.error(Messages.ERR_CANNOT_READ_MANY_USER_ORDERS, ex);
            throw new DBException(Messages.ERR_CANNOT_READ_MANY_USER_ORDERS, ex);
        } finally {
            dbManager.close(connection, preparedStatement, resultSet);
        }
        return userOrders;
    }

    @Override
    public void update(UserOrder object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_UPDATE_USER_ORDER, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_USER_ORDER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void customUpdate(UserOrder object, String daoQuery, Object... params) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_UPDATE_USER_ORDER, ex);
            throw new DBException(Messages.ERR_CANNOT_UPDATE_USER_ORDER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public void delete(UserOrder object) throws AppException {
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
            LOG.error(Messages.ERR_CANNOT_DELETE_USER_ORDER, ex);
            throw new DBException(Messages.ERR_CANNOT_DELETE_USER_ORDER, ex);
        } finally {
            dbManager.close(connection, preparedStatement, null);
        }
    }

    @Override
    public String getQuery(String daoQuery) {
        return queryMap.get(daoQuery);
    }

    public static synchronized UserOrderDao getInstance() {
        if (instance == null) {
            instance = new UserOrderDao();
        }
        return instance;
    }
}
