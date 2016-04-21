/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.persistence;

import com.bezirk.sadl.SadlRegistry;
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
    private final IDatabaseConnection dbConnection;
    /**
     * Sphere Registry - storing sphere related maps
     */
    private SphereRegistry sphereRegistry;
    /**
     * Sadl Registry - storing sadl related maps.
     */
    private SadlRegistry sadlRegistry;
    /**
     * Uhu Proxy Registry -  Stores only UhuServiceIds. It will be used only on the PC side
     */
    private UhuProxyRegistry uhuProxyRegistry;


    protected DatabaseHelper(IDatabaseConnection dbConnection) {
        super();
        this.dbConnection = dbConnection;
        this.sphereRegistry = null;
        this.sadlRegistry = null;
        this.uhuProxyRegistry = null;
    }

    /**
     * Updates the only row in the database based on the column name
     *
     * @param columnName Name of the column, @see com.bosch.upa.uhu.persistence.DB_Constants
     * @throws NullPointerException if sadlRegistry or sphereRegistry is null
     * @throws SQLException         something goes wrong while storing
     * @throws IOException          if connection to the database is not successful.
     * @throws Exception
     */
    protected void updateRegistry(String columnName) throws NullPointerException, SQLException, IOException, Exception {
        UpdateBuilder<UhuRegistry, Integer> updateDb = dbConnection.getPersistenceDAO().updateBuilder();
        switch (columnName) {
            case DBConstants.COLUMN_1:
                if (null == sadlRegistry) {
                    throw new NullPointerException("Sadl Registry cant be null");
                }
                updateDb.updateColumnValue(DBConstants.COLUMN_1, sadlRegistry);
                break;
            case DBConstants.COLUMN_2:
                if (null == sphereRegistry) {
                    throw new NullPointerException("Sphere Registry cant be null");
                }
                updateDb.updateColumnValue(DBConstants.COLUMN_2, sphereRegistry);
                break;
            case DBConstants.COLUMN_3:
                if (null == uhuProxyRegistry) {
                    throw new NullPointerException("UhuProxy Registry cant be null");
                }
                updateDb.updateColumnValue(DBConstants.COLUMN_3, uhuProxyRegistry);
                break;

            default:
        }
        updateDb.update();
    }

    /**
     * Loads the registry
     *
     * @throws NullPointerException if sadlRegistry or sphereRegistry is null
     * @throws SQLException         something goes wrong while storing
     * @throws IOException          if connection to the database is not successful.
     */
    protected void loadRegistry() throws NullPointerException, IOException, Exception {
        QueryBuilder<UhuRegistry, Integer> queryBuilder = dbConnection.getPersistenceDAO().queryBuilder();
        UhuRegistry tempRegistry = queryBuilder.queryForFirst();
        sadlRegistry = tempRegistry.getSadlRegistry();
        sphereRegistry = tempRegistry.getSphereRegistry();
        uhuProxyRegistry = tempRegistry.getUhuProxyRegistry();
    }

    /**
     * Checks the database version, if there is mismatch drop the table. If the table doesnt exists create a table and updates
     * the row.
     *
     * @param DB_VERSION Version of the database
     * @return true if version is matched and table is created, false otherwise
     * @throws NullPointerException if the connection to the database is null
     * @throws SQLException         something goes wrong while creating the table
     * @throws IOException          if connection to the database is not successful.
     * @throws Exception
     */
    protected boolean checkDatabase(final String DB_VERSION) throws NullPointerException, SQLException, IOException, Exception {
        if (!DBConstants.DB_VERSION.equals(DB_VERSION)) {
            dropTable();
        }
        if (dbConnection != null && !dbConnection.getPersistenceDAO().isTableExists()) {
            TableUtils.createTable(dbConnection.getDatabaseConnection(), UhuRegistry.class);
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
     * @throws Exception
     */
    private void dropTable() throws NullPointerException, SQLException, IOException, Exception {
        if (dbConnection != null) {
            TableUtils.dropTable(dbConnection.getDatabaseConnection(), UhuRegistry.class, true);
        }
    }

    /**
     * Insert the only row into the database
     */
    private void insertInitialRow() throws NullPointerException, SQLException, IOException, Exception {
        dbConnection.getPersistenceDAO().createOrUpdate(new UhuRegistry(1, new SadlRegistry(), new SphereRegistry(), new UhuProxyRegistry()));
    }

    /**
     * Returns the Sphere Registry to the child classes
     *
     * @return sphereRegistry
     */
    protected SphereRegistry getSphereRegistry() {
        return sphereRegistry;
    }

    /**
     * Returns the Sadl Registry to the child class
     *
     * @return sadlRegistry
     */
    protected SadlRegistry getSadlRegistry() {
        return sadlRegistry;
    }

    /**
     * Returns UhuProxyRegisty associated with the Proxy
     *
     * @return UhuProxyRegistry
     */
    protected UhuProxyRegistry getUhuProxyRegistry() {
        return uhuProxyRegistry;
    }

    /**
     * Clear the maps of the all the registry
     */
    protected void clearPersistence() throws NullPointerException, SQLException, IOException, Exception {
        sadlRegistry.clearRegistry();
        sphereRegistry.clearRegistry();
        uhuProxyRegistry.clearRegistry();
    }
}
