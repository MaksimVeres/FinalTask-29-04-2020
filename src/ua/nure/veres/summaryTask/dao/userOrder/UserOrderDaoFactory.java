package ua.nure.veres.summaryTask.dao.userOrder;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.UserOrder;

/**
 * UserOrder DAO factory.
 *
 * @see ua.nure.veres.summaryTask.dao.DaoFactory
 */
public class UserOrderDaoFactory implements DaoFactory {

    @SuppressWarnings("unchecked")
    @Override
    public Dao<UserOrder, Long> getDao() {
        return UserOrderDao.getInstance();
    }
}
