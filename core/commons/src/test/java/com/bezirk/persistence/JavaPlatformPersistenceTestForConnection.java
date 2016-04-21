package com.bezirk.persistence;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class JavaPlatformPersistenceTestForConnection {

    @Test(expected = IOException.class)
    public void testForNullConnection() throws Exception {
        String DBPath = null;
        IDatabaseConnection connection = new DatabaseConnectionForJava(DBPath);
        connection.getPersistenceDAO();
    }

    @Test
    public void testForValidConnection() throws Exception {
        String DBPath = "./";
        IDatabaseConnection connection = new DatabaseConnectionForJava(DBPath);
        assertNotNull(connection.getPersistenceDAO());
    }
}
