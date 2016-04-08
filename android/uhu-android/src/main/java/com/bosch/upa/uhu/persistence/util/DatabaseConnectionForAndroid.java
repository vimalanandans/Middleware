package com.bosch.upa.uhu.persistence.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bosch.upa.uhu.persistence.DBConstants;
import com.bosch.upa.uhu.persistence.IDatabaseConnection;
import com.bosch.upa.uhu.persistence.UhuRegistry;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by hkh5kor on 10/7/2014.
 */
public class DatabaseConnectionForAndroid extends OrmLiteSqliteOpenHelper implements IDatabaseConnection {
    private final Logger log = LoggerFactory.getLogger(DatabaseConnectionForAndroid.class);

    private ConnectionSource dbConnectionSource;
    private Dao<UhuRegistry,Integer> uhuPersistenceDao;
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;

    public DatabaseConnectionForAndroid(Context context) {
        super(context, context.getFilesDir().getPath()+File.separator+ DBConstants.DB_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public ConnectionSource getDatabaseConnection() throws NullPointerException, SQLException, IOException, Exception {
        if(null == dbConnectionSource)
        dbConnectionSource = getPersistenceDAO().getConnectionSource();
        return dbConnectionSource;
    }

    @Override
    public Dao<UhuRegistry, Integer> getPersistenceDAO() throws NullPointerException, SQLException, IOException, Exception {
        if(null == uhuPersistenceDao){
            uhuPersistenceDao = getDao(UhuRegistry.class);
            uhuPersistenceDao.setAutoCommit(true);
        }
        return uhuPersistenceDao;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        dbConnectionSource = connectionSource;
        String internalMemoryPath = mContext.getFilesDir().getPath()+File.separator+ DBConstants.DB_FILE_NAME;
        File tempDbFile = new File(internalMemoryPath);
        if(!tempDbFile.exists()){
            try {
                tempDbFile.createNewFile();
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        //To be implemented
    }

}
