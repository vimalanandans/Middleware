/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.middleware.core.datastorage;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Common Interface that is used to make connection to the database using Ormlite.
 */
public interface DatabaseConnection {
    /**
     * Returns the ConnectionSource of the Database Connection.
     *
     * @return Connection Source of the Database Connection.
     * @throws NullPointerException if connection is null
     * @throws SQLException         if connection is null
     */
    ConnectionSource getDatabaseConnection() throws NullPointerException, SQLException, IOException;

    /**
     * Returns the DAO class associated with the entity
     *
     * @return DAO associated with the Entity
     * @throws NullPointerException if connection is null
     * @throws SQLException         if connection is not possible or some thing happens while getting the DAO
     * @throws IOException          if the database file is not present
     */
    Dao<PersistenceRegistry, Integer> getPersistenceDAO() throws NullPointerException, SQLException, IOException;
}
