/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.persistence;

import com.bezirk.pubsubbroker.SadlRegistry;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This common Registry class for the Bezirk platform. It implements all the interfaces for different modules
 * of the Bezirk that needs to be persisted. The different layers will get the corresponding interfaces through
 * which they can load/ save the data to the persistence
 */
public class RegistryPersistence extends DatabaseHelper implements SadlPersistence, SpherePersistence, BezirkProxyPersistence {

    public RegistryPersistence(DatabaseConnection dbConnection, String DBVersion) throws NullPointerException, SQLException, IOException, Exception {
        super(dbConnection);
        checkDatabase(DBVersion);
    }

    @Override
    public void persistSphereRegistry() throws Exception {
        updateRegistry(DBConstants.COLUMN_2);
    }

    @Override
    public SphereRegistry loadSphereRegistry() throws Exception {
        if (null == getSphereRegistry()) {
            loadRegistry();
        }
        return getSphereRegistry();
    }

    @Override
    public void persistSadlRegistry() throws Exception {
        updateRegistry(DBConstants.COLUMN_1);
    }

    @Override
    public SadlRegistry loadSadlRegistry() throws Exception {
        if (null == getSadlRegistry()) {
            loadRegistry();
        }
        return getSadlRegistry();
    }

    @Override
    public BezirkProxyRegistry loadBezirkProxyRegistry() throws Exception {
        if (null == getBezirkProxyRegistry()) {
            loadRegistry();
        }
        return getBezirkProxyRegistry();
    }


    @Override
    public void persistBezirkProxyRegistry() throws Exception {
        updateRegistry(DBConstants.COLUMN_3);
    }

    /* (non-Javadoc)
     * @see DatabaseHelper#clearPersistence()
     */
    public void clearPersistence() throws NullPointerException, SQLException, IOException, Exception {
        super.clearPersistence();
    }
}
