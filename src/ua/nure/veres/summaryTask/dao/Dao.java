package ua.nure.veres.summaryTask.dao;

import ua.nure.veres.summaryTask.db.entity.Entity;
import ua.nure.veres.summaryTask.exception.AppException;

import java.util.List;

/**
 * Interface that provides methods to DAO.
 *
 * @param <K> Type of entity
 * @param <V> Id field type of entity
 */
public interface Dao<K extends Entity, V> {
    /*
        *DAOQuery commands:
            **All parameters need to be supported by specified DAO implementation.
            ***Parameters that a referred to another entities must have syntax like : <[EntityName].[ParameterName]>,
               example: READ MANY User.login

         1) CREATE:
            Declares to create entity in database.
            Type: hidden - command executes automatically.

            Syntax:
                "CREATE"

            Example:
                DaoFactory factory = new UserDaoFactory();
                Dao userDao = factory.getDao();

                User user = new User("User-login", "User-role", 100);

                userDao.create(user);

         2) READ:
            2.1) Single mode
                 Declares to read a single entity from database.
                 Type: open with mode and unique parameter/parameters.

                 Syntax:
                    "READ ONE <unique_parameter>"

                 Example:
                      DaoFactory factory = new UserDaoFactory();
                      Dao userDao = factory.getDao();

                      int id = 3;
                      User user = userDao.customReadOne("READ ONE id", id);

            2.2) Multi mode
                 Declares to read many entities from database.
                 Type: open with mode and parameter/parameters.

                 Syntax:
                    "READ MANY <parameter>"

                 Example:
                      DaoFactory factory = new UserDaoFactory();
                      Dao userDao = factory.getDao();

                      String firstName = "SomeFirstName";
                      List<User> users = userDao.customReadMany("READ MANY firstName", firstName);

          3) UPDATE:
             Declares to update entity in database.
             Type: open with automatically or manual parameter selection.

                Syntax:
                    "UPDATE"
                    "UPDATE <parameter>"

                Example:
                    DaoFactory factory = new UserDaoFactory();
                    Dao userDao = factory.getDao();

                    User user = new User("User-login", "User-role", 100);
                    String newLogin = "New-login";
                    userDao.customUpdate(user, "UPDATE login", newLogin);

          4) DELETE:
             Declares to delete entity in database.
             Type: hidden - command executes automatically.

                Syntax:
                    "DELETE"

                Example:
                    DaoFactory factory = new UserDaoFactory();
                    Dao userDao = factory.getDao();

                    User user = new User();
                    user.setId(15);
                    userDao.delete(user);
     */

    /**
     * Creates an entity in a database.
     *
     * @param object Entity to create
     */
    void create(K object) throws AppException;

    /**
     * Reads one entity from a database by it's id.
     *
     * @param id id of the entity
     * @return Entity from the database
     */
    K read(V id) throws AppException;

    /**
     * Reads all of entities from a database.
     *
     * @return List of Entity from the database
     */
    List<K> read() throws AppException;

    /**
     * Reads an entity from a database by custom DAOQuery*.
     *
     * @param daoQuery DAOQuery* to execute
     * @param params   params of DAOQuery* statement
     * @return Entity from the database
     */
    K customReadOne(String daoQuery, Object... params) throws AppException;

    /**
     * Reads many entities from a database by custom DAOQuery*.
     *
     * @param daoQuery DAOQuery* to execute
     * @param params   params of DAOQuery* statement
     * @return List of entities from the database
     */
    List<K> customReadMany(String daoQuery, Object... params) throws AppException;

    /**
     * Does update of an entity in database.
     *
     * @param object Entity to update
     */
    void update(K object) throws AppException;

    /**
     * Does custom update of an entity in database by custom DAOQuery*.
     *
     * @param object   Entity to update
     * @param daoQuery DAOQuery* to execute
     * @param params   params of DAOQuery* statement
     */
    void customUpdate(K object, String daoQuery, Object... params) throws AppException;

    /**
     * Deletes an entity in a database.
     *
     * @param object Entity to delete
     */
    void delete(K object) throws AppException;

    /**
     * Converts DAOQuery* to equal SQL command.
     *
     * @param daoQuery DAOQuery* to convert.
     * @return SQL command.
     */
    String getQuery(String daoQuery);

}
