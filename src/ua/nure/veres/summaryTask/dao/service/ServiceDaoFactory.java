package ua.nure.veres.summaryTask.dao.service;

import ua.nure.veres.summaryTask.dao.Dao;
import ua.nure.veres.summaryTask.dao.DaoFactory;
import ua.nure.veres.summaryTask.db.entity.Service;

/**
 * Service DAO factory.
 *
 * @see ua.nure.veres.summaryTask.dao.DaoFactory
 */
public class ServiceDaoFactory implements DaoFactory {

    @SuppressWarnings("unchecked")
    @Override
    public Dao<Service, Long> getDao() {
        return ServiceDao.getInstance();
    }

}
