package com.bosch.upa.uhu.persistence;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public final class DatabaseConnectionForJava implements IDatabaseConnection {

    private final String dbFilePath;
    private Dao<UhuRegistry, Integer> uhuPersistenceDao;

    public DatabaseConnectionForJava(String dbFileLocation) {
        dbFilePath = dbFileLocation;
    }

    @Override
    public ConnectionSource getDatabaseConnection()
            throws NullPointerException, SQLException, IOException {
        setupDatabase();
        return new JdbcConnectionSource(
                DBConstants.DB_URL_PATH + dbFilePath + File.separator
                        + DBConstants.DB_FILE_NAME);
    }

    @Override
    public Dao<UhuRegistry, Integer> getPersistenceDAO()
            throws NullPointerException, SQLException, IOException, Exception {
        if (null == uhuPersistenceDao) {
            uhuPersistenceDao = DaoManager.createDao(getDatabaseConnection(),
                    UhuRegistry.class);
        }
        return uhuPersistenceDao;
    }

    private void setupDatabase() throws IOException {
        final File tempDBFile = new File(dbFilePath + File.separator
                + DBConstants.DB_FILE_NAME);
        if (!tempDBFile.exists()) {
            tempDBFile.createNewFile();
        }
    }

}
