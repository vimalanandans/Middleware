package com.bezirk.persistence;

import static org.junit.Assert.assertNotNull;

public class JavaPlatformPersistenceTestForConnection {

//    @Test(expected = IOException.class)
    public void testForNullConnection() throws Exception {
        String DBPath = null;
        com.bezirk.datastorage.DatabaseConnection connection = new DatabaseConnectionForJava(DBPath);
        connection.getPersistenceDAO();
    }

//    @Test
    public void testForValidConnection() throws Exception {
        String DBPath = "./";
        com.bezirk.datastorage.DatabaseConnection connection = new DatabaseConnectionForJava(DBPath);
        assertNotNull(connection.getPersistenceDAO());
    }
}
