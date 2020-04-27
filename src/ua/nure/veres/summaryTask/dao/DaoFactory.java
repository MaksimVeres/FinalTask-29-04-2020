package ua.nure.veres.summaryTask.dao;

import ua.nure.veres.summaryTask.db.entity.Entity;

/**
 * Abstract DAO factory.
 */
public interface DaoFactory {

    <K extends Entity, V> Dao<K, V> getDao();

}
