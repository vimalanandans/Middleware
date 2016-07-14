package com.bezirk.persistence;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public final class DatabaseConnectionForJava implements DatabaseConnection {

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
            tempDBFile.createNewFile();
        }
    }

}
