package com.bezirk.middleware.java.persistence;

import com.bezirk.middleware.core.datastorage.DatabaseConnection;
import com.bezirk.middleware.core.datastorage.PersistenceConstants;
import com.bezirk.middleware.core.datastorage.PersistenceRegistry;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public final class DatabaseConnectionForJava implements DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionForJava.class);

    private final String dbFilePath;
    private Dao<PersistenceRegistry, Integer> bezirkPersistenceDao;

    public DatabaseConnectionForJava(String dbFileLocation) {
        dbFilePath = dbFileLocation;
    }

    @Override
    public ConnectionSource getDatabaseConnection()
            throws NullPointerException, SQLException, IOException {
        setupDatabase();
        return new JdbcConnectionSource(
                PersistenceConstants.DB_URL_PATH + dbFilePath + File.separator
                        + PersistenceConstants.DB_FILE_NAME);
    }

    @Override
    public Dao<PersistenceRegistry, Integer> getPersistenceDAO()
            throws NullPointerException, SQLException, IOException {
        if (null == bezirkPersistenceDao) {
            bezirkPersistenceDao = DaoManager.createDao(getDatabaseConnection(),
                    PersistenceRegistry.class);
        }
        return bezirkPersistenceDao;
    }

    private void setupDatabase() throws IOException {
        final File tempDBFile = new File(dbFilePath + File.separator
                + PersistenceConstants.DB_FILE_NAME);
        if (!tempDBFile.exists()) {
            if (!tempDBFile.createNewFile()) {
                logger.error("Failed to create database file{}", tempDBFile.getAbsolutePath());
            }
        }
    }

}
