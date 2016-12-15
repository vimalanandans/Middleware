/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.datastorage;

import com.bezirk.middleware.core.pubsubbroker.PubSubBrokerRegistry;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.TableUtils;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Database Helper class that manages the Database operations (CRUD Operations)
 */
public class DatabaseHelper {
    /**
     * Platform specific Database Connection
     */
    private final DatabaseConnection dbConnection;
    /**
     * sphere Registry - storing sphere related maps
     */
    private SphereRegistry sphereRegistry;
    /**
     * Sadl Registry - storing sadl related maps.
     */
    private PubSubBrokerRegistry pubSubBrokerRegistry;
    /**
     * Bezirk Proxy Registry -  Stores only BezirkServiceIds. It will be used only on the PC side
     */
    private ProxyRegistry proxyRegistry;


    protected DatabaseHelper(DatabaseConnection dbConnection) {
        super();
        this.dbConnection = dbConnection;
        this.sphereRegistry = null;
        this.pubSubBrokerRegistry = null;
        this.proxyRegistry = null;
    }

    /**
     * Updates the only row in the database based on the column name
     *
     * @param columnName Name of the column, @see com.bosch.upa.uhu.persistence.DB_Constants
     * @throws NullPointerException if pubSubBrokerRegistry or sphereRegistry is null
     * @throws SQLException         something goes wrong while storing
     * @throws IOException          if connection to the database is not successful.
     */
    protected void updateRegistry(String columnName) throws SQLException, IOException {
        UpdateBuilder<PersistenceRegistry, Integer> updateDb = dbConnection.getPersistenceDAO().updateBuilder();
        switch (columnName) {
            case PersistenceConstants.COLUMN_1:
                if (null == pubSubBrokerRegistry) {
                    throw new NullPointerException("Sadl Registry cant be null");
                }
                updateDb.updateColumnValue(PersistenceConstants.COLUMN_1, pubSubBrokerRegistry);
                break;
            case PersistenceConstants.COLUMN_2:
                if (null == sphereRegistry) {
                    throw new NullPointerException("sphere Registry cant be null");
                }
                updateDb.updateColumnValue(PersistenceConstants.COLUMN_2, sphereRegistry);
                break;
            case PersistenceConstants.COLUMN_3:
                if (null == proxyRegistry) {
                    throw new NullPointerException("BezirkProxy Registry cant be null");
                }
                updateDb.updateColumnValue(PersistenceConstants.COLUMN_3, proxyRegistry);
                break;

            default:
        }
        updateDb.update();
    }

    /**
     * Loads the registry
     *
     * @throws NullPointerException if pubSubBrokerRegistry or sphereRegistry is null
     * @throws SQLException         something goes wrong while storing
     * @throws IOException          if connection to the database is not successful.
     */
    protected void loadRegistry() throws SQLException, IOException {
        QueryBuilder<PersistenceRegistry, Integer> queryBuilder = dbConnection.getPersistenceDAO().queryBuilder();
        PersistenceRegistry tempRegistry = queryBuilder.queryForFirst();
        pubSubBrokerRegistry = tempRegistry.getPubSubBrokerRegistry();
        sphereRegistry = tempRegistry.getSphereRegistry();
        proxyRegistry = tempRegistry.getProxyRegistry();
    }

    /**
     * Checks the database version, if there is mismatch drop the table. If the table doesn't exists
     * create a table and updates the row.
     *
     * @param DB_VERSION Version of the database
     * @return true if version is matched and table is created, false otherwise
     * @throws NullPointerException if the connection to the database is null
     * @throws SQLException         something goes wrong while creating the table
     * @throws IOException          if connection to the database is not successful.
     */
    protected boolean checkDatabase(final String DB_VERSION) throws SQLException, IOException {
        if (!PersistenceConstants.DB_VERSION.equals(DB_VERSION)) {
            dropTable();
        }
        if (dbConnection != null && !dbConnection.getPersistenceDAO().isTableExists()) {
            TableUtils.createTable(dbConnection.getDatabaseConnection(), PersistenceRegistry.class);
            insertInitialRow();
        }
        return true;
    }

    /**
     * Drops the database table. This is useful when we change the database version
     *
     * @throws NullPointerException if the connection to the database is null
     * @throws SQLException         something goes wrong while creating the table
     * @throws IOException          if connection to the database is not successful.
     */
    private void dropTable() throws SQLException, IOException {
        if (dbConnection != null) {
            TableUtils.dropTable(dbConnection.getDatabaseConnection(), PersistenceRegistry.class, true);
        }
    }

    /**
     * Insert the only row into the database
     */
    private void insertInitialRow() throws SQLException, IOException {
        dbConnection.getPersistenceDAO().createOrUpdate(
                new PersistenceRegistry(1, new PubSubBrokerRegistry(), new SphereRegistry(), new ProxyRegistry()));
    }

    /**
     * Returns the sphere Registry to the child classes
     *
     * @return sphereRegistry
     */
    protected SphereRegistry getSphereRegistry() {
        return sphereRegistry;
    }

    /**
     * Returns the Sadl Registry to the child class
     *
     * @return pubSubBrokerRegistry
     */
    protected PubSubBrokerRegistry getPubSubBrokerRegistry() {
        return pubSubBrokerRegistry;
    }

    /**
     * Returns ProxyRegistry associated with the Proxy
     *
     * @return ProxyRegistry
     */
    protected ProxyRegistry getProxyRegistry() {
        return proxyRegistry;
    }

    /**
     * Clear the maps of the all the registry
     */
    protected void clearPersistence() {
        pubSubBrokerRegistry.clearRegistry();
        sphereRegistry.clearRegistry();
        proxyRegistry.clearRegistry();
    }
}
