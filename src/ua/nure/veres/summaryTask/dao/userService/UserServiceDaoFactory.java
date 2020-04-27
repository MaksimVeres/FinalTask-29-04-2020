package ua.nure.veres.summaryTask.dao.userService;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.UserService;

/**
 * UserService DAO factory.
 *
 * @see ua.nure.veres.summaryTask.dao.DaoFactory
 */
public class UserServiceDaoFactory implements DaoFactory {

    @SuppressWarnings("unchecked")
    @Override
    public Dao<UserService, Long> getDao() {
        return UserServiceDao.getInstance();
    }
}
