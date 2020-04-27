package ua.nure.veres.summaryTask.dao.tariff;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.Tariff;

/**
 * Tariff DAO factory.
 *
 * @see ua.nure.veres.summaryTask.dao.DaoFactory
 */
public class TariffDaoFactory implements DaoFactory {

    @SuppressWarnings("unchecked")
    @Override
    public Dao<Tariff, Long> getDao() {
        return TariffDao.getInstance();
    }
}
