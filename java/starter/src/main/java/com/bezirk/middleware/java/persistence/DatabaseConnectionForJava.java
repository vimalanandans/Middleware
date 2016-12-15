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
            throws SQLException, IOException {
        setupDatabase();
        return new JdbcConnectionSource(
                PersistenceConstants.DB_URL_PATH + dbFilePath + File.separator
                        + PersistenceConstants.DB_FILE_NAME);
    }

    @Override
    public Dao<PersistenceRegistry, Integer> getPersistenceDAO()
            throws SQLException, IOException {
        if (null == bezirkPersistenceDao) {
            bezirkPersistenceDao = DaoManager.createDao(getDatabaseConnection(),
                    PersistenceRegistry.class);
        }
        return bezirkPersistenceDao;
    }

    private void setupDatabase() throws IOException {
        final File tempDBFile = new File(dbFilePath + File.separator
                + PersistenceConstants.DB_FILE_NAME);
        if (!tempDBFile.exists() && !tempDBFile.createNewFile()) {
            logger.error("Failed to create database file{}", tempDBFile.getAbsolutePath());
        }
    }

}
