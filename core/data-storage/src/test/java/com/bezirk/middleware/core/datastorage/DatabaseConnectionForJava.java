package com.bezirk.middleware.core.datastorage;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;


public final class DatabaseConnectionForJava implements DatabaseConnection {

    private final String dbFilePath;
    private ConnectionSource dbConnectionSource = null;
    private Dao<PersistenceRegistry, Integer> bezirkPersistenceDao = null;

    public DatabaseConnectionForJava(String dbFileLocation) throws IOException {
        dbFilePath = dbFileLocation;
        setupDatabase();
    }

    public ConnectionSource getDatabaseConnection() throws NullPointerException, SQLException, IOException {

        dbConnectionSource = new JdbcConnectionSource(PersistenceConstants.DB_URL_PATH + dbFilePath + "/" + PersistenceConstants.DB_FILE_NAME);
        return dbConnectionSource;
    }

    public Dao<PersistenceRegistry, Integer> getPersistenceDAO() throws NullPointerException, SQLException, IOException {
        if (null == bezirkPersistenceDao) {
            bezirkPersistenceDao = DaoManager.createDao(getDatabaseConnection(), PersistenceRegistry.class);
            bezirkPersistenceDao.setAutoCommit(true);
        }
        return bezirkPersistenceDao;
    }

    private void setupDatabase() throws IOException {
        File tempDBFile = new File(dbFilePath + "/" + PersistenceConstants.DB_FILE_NAME);
        tempDBFile.setWritable(true);
        System.out.println("FilePaht" + tempDBFile.getAbsolutePath());
        if (!tempDBFile.exists()) {
            tempDBFile.createNewFile();
        }
    }

}
