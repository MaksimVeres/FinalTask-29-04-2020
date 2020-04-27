package ua.nure.veres.summaryTask.dao.userDescribe;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.UserDescribe;

/**
 * UserDescribe DAO factory.
 *
 * @see ua.nure.veres.summaryTask.dao.DaoFactory
 */
public class UserDescribeDaoFactory implements DaoFactory {

    @SuppressWarnings("unchecked")
    @Override
    public Dao<UserDescribe, Long> getDao() {
        return UserDescribeDao.getInstance();
    }
}
