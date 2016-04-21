/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.persistence;

import com.bezirk.sadl.SadlRegistry;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This common Registry class for the Uhu platform. It implements all the interfaces for different modules
 * of the Uhu that needs to be persisted. The different layers will get the corrosponding interfaces through
 * which they can load/ save the data to the persistence
 */
public class RegistryPersistence extends DatabaseHelper implements ISadlPersistence, ISpherePersistence, IUhuProxyPersistence {

    public RegistryPersistence(IDatabaseConnection dbConnection, String DBVesion) throws NullPointerException, SQLException, IOException, Exception {
        super(dbConnection);
        checkDatabase(DBVesion);
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
    public UhuProxyRegistry loadUhuProxyRegistry() throws Exception {
        if (null == getUhuProxyRegistry()) {
            loadRegistry();
        }
        return getUhuProxyRegistry();
    }


    @Override
    public void persistUhuProxyRegistry() throws Exception {
        updateRegistry(DBConstants.COLUMN_3);
    }

    /* (non-Javadoc)
     * @see DatabaseHelper#clearPersistence()
     */
    public void clearPersistence() throws NullPointerException, SQLException, IOException, Exception {
        super.clearPersistence();
    }
}
