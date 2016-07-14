package com.bezirk.persistence.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bezirk.persistence.PersistenceConstants;
import com.bezirk.persistence.DatabaseConnection;
import com.bezirk.persistence.PersistenceRegistry;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConnectionForAndroid extends OrmLiteSqliteOpenHelper implements DatabaseConnection {
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;
    private ConnectionSource dbConnectionSource;
    private Dao<PersistenceRegistry, Integer> bezirkPersistenceDao;

    public DatabaseConnectionForAndroid(Context context) {
        super(context, context.getFilesDir().getPath() + File.separator + PersistenceConstants.DB_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public ConnectionSource getDatabaseConnection() throws NullPointerException, SQLException, IOException {
        if (null == dbConnectionSource)
            dbConnectionSource = getPersistenceDAO().getConnectionSource();
        return dbConnectionSource;
    }

    @Override
    public Dao<PersistenceRegistry, Integer> getPersistenceDAO() throws NullPointerException, SQLException, IOException {
        if (null == bezirkPersistenceDao) {
            bezirkPersistenceDao = getDao(PersistenceRegistry.class);
            bezirkPersistenceDao.setAutoCommit(true);
        }
        return bezirkPersistenceDao;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        dbConnectionSource = connectionSource;
        String internalMemoryPath = mContext.getFilesDir().getPath() + File.separator + PersistenceConstants.DB_FILE_NAME;
        File tempDbFile = new File(internalMemoryPath);
        if (!tempDbFile.exists()) {
            try {
                tempDbFile.createNewFile();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        //To be implemented
    }

}
