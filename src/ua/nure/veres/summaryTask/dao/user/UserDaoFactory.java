package ua.nure.veres.summaryTask.dao.user;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.User;

/**
 * User DAO Factory.
 *
 * @see ua.nure.veres.summaryTask.dao.DaoFactory
 */
public class UserDaoFactory implements DaoFactory {

    @SuppressWarnings("unchecked")
    @Override
    public Dao<User, Long> getDao() {
        return UserDao.getInstance();
    }
}
