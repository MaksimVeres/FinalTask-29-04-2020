package ua.nure.veres.summaryTask.db;

import org.apache.log4j.Logger;
import ua.nure.veres.summaryTask.exception.db.DBException;
import ua.nure.veres.summaryTask.exception.Messages;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DB manager. Works with DB.
 */
public class DBManager {

    private DataSource dataSource;

    private static final Logger LOG = Logger.getLogger(DBManager.class);

    private static DBManager instance;

    private DBManager() throws DBException {
        dataSource = getDataSource();
        LOG.trace("DataSource ==> " + dataSource);
    }

    /**
     * Returns an datasource to a database.
     * @return DataSource
     */
    private DataSource getDataSource() throws DBException {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            //providerDB - the name of data source
            return (DataSource) envContext.lookup("jdbc/providerDB");
        } catch (NamingException ex) {
            LOG.error(Messages.ERR_CANNOT_OBTAIN_DATA_SOURCE, ex);
            throw new DBException(Messages.ERR_CANNOT_OBTAIN_DATA_SOURCE, ex);
        }
    }

    /**
     * Returns a DB connection from the Pool Connections. Before using this
     * method you must configure the Date Source and the Connections Pool in
     * your WEB_APP_ROOT/META-INF/context.xml file.
     *
     * @return DB connection.
     */
    public Connection getConnection() throws DBException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException ex) {
            LOG.error(Messages.ERR_CANNOT_OBTAIN_CONNECTION, ex);
            throw new DBException(Messages.ERR_CANNOT_OBTAIN_CONNECTION, ex);
        }
        return connection;
    }

    // //////////////////////////////////////////////////////////
    // DB Util methods
    // //////////////////////////////////////////////////////////

    /**
     * Closes a connection.
     *
     * @param connection connection need to be closed.
     */
    private void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                LOG.error(Messages.ERR_CANNOT_CLOSE_CONNECTION);
            }
        }
    }

    /**
     * Closes a statement.
     *
     * @param statement statement need to be closed.
     */
    private void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ex) {
                LOG.error(Messages.ERR_CANNOT_CLOSE_STATEMENT);
            }
        }
    }

    /**
     * Closes a result set.
     *
     * @param resultSet resultSet need to be closed.
     */
    private void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException ex) {
                LOG.error(Messages.ERR_CANNOT_CLOSE_RESULT_SET);
            }
        }
    }

    /**
     * Closes a resources.
     *
     * @param connection connection need to be closed.
     * @param statement  statement need to be closed.
     * @param resultSet  resultSet need to be closed.
     */
    public void close(Connection connection, Statement statement, ResultSet resultSet) {
        close(resultSet);
        close(statement);
        close(connection);
    }

    /**
     * Rollbacks a connection.
     *
     * @param connection Connection to be rolled back.
     */
    public void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                LOG.error(Messages.ERR_CANNOT_ROLLBACK_TRANSACTION, ex);
            }
        }
    }

    //----------------- END: DB Util methods


    // //////////////////////////////////////////////////////////
    // Singleton
    // //////////////////////////////////////////////////////////

    /**
     * Singleton method to return instance of DBManager.
     *
     * @return instance of DBManager.
     * @throws DBException if instantiation of instance was failed.
     */
    public static synchronized DBManager getInstance() throws DBException {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    //-----------------END: Singleton

}
