/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.middleware.core.datastorage;

import com.bezirk.middleware.core.pubsubbroker.PubSubBrokerRegistry;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This common Registry class for the Bezirk platform. It implements all the interfaces for different modules
 * of the Bezirk that needs to be persisted. The different layers will get the corresponding interfaces through
 * which they can load/ save the data to the persistence
 */
public class RegistryStorage extends DatabaseHelper implements PubSubBrokerStorage, SpherePersistence, ProxyPersistence {

    public RegistryStorage(DatabaseConnection dbConnection, String DBVersion) throws DataStorageException {
        super(dbConnection);
        try {
            checkDatabase(DBVersion);
        } catch (SQLException | IOException e) {
            throw new DataStorageException("Database version checked failed when constructing " +
                    "registry storage", e);
        }
    }

    @Override
    public void persistSphereRegistry() throws DataStorageException {
        try {
            updateRegistry(PersistenceConstants.COLUMN_2);
        } catch (SQLException | IOException e) {
            throw new DataStorageException("Failed to persist sphere registry", e);
        }
    }

    @Override
    public SphereRegistry loadSphereRegistry() throws DataStorageException {
        if (null == getSphereRegistry()) {
            try {
                loadRegistry();
            } catch (SQLException | IOException e) {
                throw new DataStorageException("Failed to load sphere registry", e);
            }
        }
        return getSphereRegistry();
    }

    @Override
    public void persistPubSubBrokerRegistry() throws Exception {
        updateRegistry(PersistenceConstants.COLUMN_1);
    }

    @Override
    public PubSubBrokerRegistry loadPubSubBrokerRegistry() throws Exception {
        if (null == getPubSubBrokerRegistry()) {
            loadRegistry();
        }
        return getPubSubBrokerRegistry();
    }

    @Override
    public ProxyRegistry loadBezirkProxyRegistry() throws DataStorageException {
        if (null == getProxyRegistry()) {
            try {
                loadRegistry();
            } catch (SQLException | IOException e) {
                throw new DataStorageException("Failed to load Bezirk proxy registry", e);
            }
        }

        return getProxyRegistry();
    }


    @Override
    public void persistBezirkProxyRegistry() throws DataStorageException {
        try {
            updateRegistry(PersistenceConstants.COLUMN_3);
        } catch (SQLException | IOException e) {
            throw new DataStorageException("Failed to persist Bezirk proxy registry", e);
        }
    }

    /* (non-Javadoc)
     * @see DatabaseHelper#clearPersistence()
     */
    public void clearPersistence() {
        super.clearPersistence();
    }
}
