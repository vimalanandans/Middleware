package com.bezirk.persistence;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class JavaPlatformPersistenceTestForConnection {

//    @Test(expected = IOException.class)
    public void testForNullConnection() throws Exception {
        String DBPath = null;
        DatabaseConnection connection = new DatabaseConnectionForJava(DBPath);
        connection.getPersistenceDAO();
    }

//    @Test
    public void testForValidConnection() throws Exception {
        String DBPath = "./";
        DatabaseConnection connection = new DatabaseConnectionForJava(DBPath);
        assertNotNull(connection.getPersistenceDAO());
    }
}
