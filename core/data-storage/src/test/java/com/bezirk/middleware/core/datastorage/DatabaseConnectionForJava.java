/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
