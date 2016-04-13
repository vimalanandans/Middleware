package com.bezirk.persistence;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;



public final class DatabaseConnectionForJava implements IDatabaseConnection {
	
	private ConnectionSource dbConnectionSource = null;
	private final String dbFilePath;
	private Dao<UhuRegistry,Integer> uhuPersistenceDao = null;
	
	public DatabaseConnectionForJava(String dbFileLocation) throws IOException {
		dbFilePath = dbFileLocation;
		setupDatabase();
	}

	public ConnectionSource getDatabaseConnection() throws NullPointerException,SQLException, IOException {
		
		dbConnectionSource = new JdbcConnectionSource(DBConstants.DB_URL_PATH+dbFilePath+"/"+DBConstants.DB_FILE_NAME);
		return dbConnectionSource;
	}

	public Dao<UhuRegistry,Integer> getPersistenceDAO() throws NullPointerException, SQLException, IOException   {
		if(null == uhuPersistenceDao){
			uhuPersistenceDao = DaoManager.createDao(getDatabaseConnection(), UhuRegistry.class);
			uhuPersistenceDao.setAutoCommit(true);
		}
		return uhuPersistenceDao;
	}

    private void setupDatabase() throws IOException{
		File tempDBFile = new File(dbFilePath+"/"+DBConstants.DB_FILE_NAME);
		tempDBFile.setWritable(true);
		System.out.println("FilePaht" + tempDBFile.getAbsolutePath());
		if(!tempDBFile.exists()){
			tempDBFile.createNewFile();
		}
	}

}