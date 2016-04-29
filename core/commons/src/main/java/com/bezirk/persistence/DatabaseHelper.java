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
    private final DatabaseConnection dbConnection;
    /**
     * sphere Registry - storing sphere related maps
     */
    private SphereRegistry sphereRegistry;
    /**
     * Sadl Registry - storing sadl related maps.
     */
    private SadlRegistry sadlRegistry;
    /**
     * Bezirk Proxy Registry -  Stores only UhuServiceIds. It will be used only on the PC side
     */
    private BezirkProxyRegistry bezirkProxyRegistry;


    protected DatabaseHelper(DatabaseConnection dbConnection) {
        super();
        this.dbConnection = dbConnection;
        this.sphereRegistry = null;
        this.sadlRegistry = null;
        this.bezirkProxyRegistry = null;
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
        UpdateBuilder<BezirkRegistry, Integer> updateDb = dbConnection.getPersistenceDAO().updateBuilder();
        switch (columnName) {
            case DBConstants.COLUMN_1:
                if (null == sadlRegistry) {
                    throw new NullPointerException("Sadl Registry cant be null");
                }
                updateDb.updateColumnValue(DBConstants.COLUMN_1, sadlRegistry);
                break;
            case DBConstants.COLUMN_2:
                if (null == sphereRegistry) {
                    throw new NullPointerException("sphere Registry cant be null");
                }
                updateDb.updateColumnValue(DBConstants.COLUMN_2, sphereRegistry);
                break;
            case DBConstants.COLUMN_3:
                if (null == bezirkProxyRegistry) {
                    throw new NullPointerException("UhuProxy Registry cant be null");
                }
                updateDb.updateColumnValue(DBConstants.COLUMN_3, bezirkProxyRegistry);
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
        QueryBuilder<BezirkRegistry, Integer> queryBuilder = dbConnection.getPersistenceDAO().queryBuilder();
        BezirkRegistry tempRegistry = queryBuilder.queryForFirst();
        sadlRegistry = tempRegistry.getSadlRegistry();
        sphereRegistry = tempRegistry.getSphereRegistry();
        bezirkProxyRegistry = tempRegistry.getBezirkProxyRegistry();
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
     * @throws Exception
     */
    protected boolean checkDatabase(final String DB_VERSION) throws NullPointerException, SQLException, IOException, Exception {
        if (!DBConstants.DB_VERSION.equals(DB_VERSION)) {
            dropTable();
        }
        if (dbConnection != null && !dbConnection.getPersistenceDAO().isTableExists()) {
            TableUtils.createTable(dbConnection.getDatabaseConnection(), BezirkRegistry.class);
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
            TableUtils.dropTable(dbConnection.getDatabaseConnection(), BezirkRegistry.class, true);
        }
    }

    /**
     * Insert the only row into the database
     */
    private void insertInitialRow() throws NullPointerException, SQLException, IOException, Exception {
        dbConnection.getPersistenceDAO().createOrUpdate(new BezirkRegistry(1, new SadlRegistry(), new SphereRegistry(), new BezirkProxyRegistry()));
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
     * @return sadlRegistry
     */
    protected SadlRegistry getSadlRegistry() {
        return sadlRegistry;
    }

    /**
     * Returns BezirkProxyRegistry associated with the Proxy
     *
     * @return BezirkProxyRegistry
     */
    protected BezirkProxyRegistry getBezirkProxyRegistry() {
        return bezirkProxyRegistry;
    }

    /**
     * Clear the maps of the all the registry
     */
    protected void clearPersistence() throws NullPointerException, SQLException, IOException, Exception {
        sadlRegistry.clearRegistry();
        sphereRegistry.clearRegistry();
        bezirkProxyRegistry.clearRegistry();
    }
}
