/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.persistence;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Common Interface that is used to make connection to the database using Ormlite.
 */
public interface IDatabaseConnection {
    /**
     * Returns the ConnectionSource of the Database Connection.
     *
     * @return Connection Source of the Database Connection.
     * @throws NullPointerException if connection is null
     * @throws SQLException         if connection is null
     * @throws IOException          if problem in connecting to the database.
     * @throws Exception
     */
    public ConnectionSource getDatabaseConnection() throws NullPointerException, SQLException, IOException, Exception;

    /**
     * Returns the DAO class associted with the entity
     *
     * @return DAO associated with the Entity
     * @throws NullPointerException if connection is null
     * @throws SQLException         if connection is not possible or some thing happens while getting the DAO
     * @throws IOException          if the database file is not present
     * @throws Exception
     */
    public Dao<UhuRegistry, Integer> getPersistenceDAO() throws NullPointerException, SQLException, IOException, Exception;
}
