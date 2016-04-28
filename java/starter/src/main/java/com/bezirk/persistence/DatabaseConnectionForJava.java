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
    private Dao<BezirkRegistry, Integer> uhuPersistenceDao;

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
    public Dao<BezirkRegistry, Integer> getPersistenceDAO()
            throws NullPointerException, SQLException, IOException, Exception {
        if (null == uhuPersistenceDao) {
            uhuPersistenceDao = DaoManager.createDao(getDatabaseConnection(),
                    BezirkRegistry.class);
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
