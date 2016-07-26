///**
// * @author Vijet Badigannavar (bvijet@in.bosch.com)
// */
//package com.bezirk.persistence;
//
//import com.j256.ormlite.table.TableUtils;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.sql.SQLException;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//
///**
// * This class test the Persistence of the BezirkServiceIdPersistence, to check loading and storing of
// * ZirkId when the zirk/s register/s and Unregister/s.
// */
//public class ZirkIdPersistenceTest {
//    String DBPath = "./";
//    DatabaseConnection dbConnection = null;
//
//    @Before
//    public void before() throws IOException {
//        dbConnection = new DatabaseConnectionForJava(DBPath);
//    }
//
//    @After
//    public void tearDown() throws NullPointerException, SQLException, IOException {
//        // Deleting the uhu_database.sqlite is not happening so after each test, I am dropping the table
//        TableUtils.dropTable(dbConnection.getDatabaseConnection(), PersistenceRegistry.class, true);
//    }
//
//    /**
//     * Checks if the ServiceIds are persisting in the ProxyPersistence. Get hold of the ProxyPersistence,
//     * Update the ProxyPersistence with sample serviceIds and persist. Shut the Bezirk or Restart the Bezirk
//     * and load the ProxyPersistence. Check if the sample serviceIds exist in the ProxyPersistence, that
//     * validates this testcase.
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testForBezirkServiceIdPersistenceRegistration() throws Exception {
//        RegistryStorage regPersistence = new RegistryStorage(dbConnection, PersistenceConstants.DB_VERSION);
//        assertNotNull(regPersistence);
//        ProxyPersistence proxyPersistence = regPersistence;
//
//        ProxyRegistry proxyRegistry = proxyPersistence.loadBezirkProxyRegistry();
//        assertNotNull(proxyRegistry);
//        assertNull(proxyRegistry.getBezirkServiceId("null"));
//        // update the registry
//        proxyRegistry.updateBezirkZirkId("zirk-A", "zid-1");
//        proxyPersistence.persistBezirkProxyRegistry();
//        proxyRegistry.updateBezirkZirkId("zirk-B", "zid-2");
//        proxyPersistence.persistBezirkProxyRegistry();
//
//        //close the db == Bezirk is stopped or Restart the Bezirk
//        dbConnection.getDatabaseConnection().close();
//        //Check if the data is persisted
//        regPersistence = new RegistryStorage(dbConnection, PersistenceConstants.DB_VERSION);
//        proxyPersistence = regPersistence;
//        proxyRegistry = proxyPersistence.loadBezirkProxyRegistry();
//
//        assertEquals(proxyRegistry.getBezirkServiceId("zirk-A"), "zid-1");
//        assertEquals(proxyRegistry.getBezirkServiceId("zirk-B"), "zid-2");
//        assertNull(proxyRegistry.getBezirkServiceId("zirk-X")); // should fail
//
//    }
//
//    /**
//     * Check the ServiceIdPersistence. Get the ProxyRegistry and update the BezirkProxyRegisty with sample
//     * ServiceIds. Then close the dbConnection that stimulates restarting or shutting of Bezirk. Delete the
//     * sample zirk Ids and shut the Bezirk. Check the registry for map that contains no zirkId that
//     * validates this test case.
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testForBezirkServiceIdPersistenceUnRegistrationWithBezirkStartUp() throws Exception {
//        RegistryStorage regPersistence = new RegistryStorage(dbConnection, PersistenceConstants.DB_VERSION);
//        assertNotNull(regPersistence);
//        ProxyPersistence proxyPersistence = regPersistence;
//
//        ProxyRegistry proxyRegistry = proxyPersistence.loadBezirkProxyRegistry();
//        assertNotNull(proxyRegistry);
//        assertNull(proxyRegistry.getBezirkServiceId("null"));
//        // update the registry
//        proxyRegistry.updateBezirkZirkId("zirk-A", "zid-1");
//        proxyPersistence.persistBezirkProxyRegistry();
//        proxyRegistry.updateBezirkZirkId("zirk-B", "zid-2");
//        proxyPersistence.persistBezirkProxyRegistry();
//
//        proxyRegistry.deleteBezirkZirkId("zirk-A");
//        proxyPersistence.persistBezirkProxyRegistry();
//
//        //close the db == Bezirk is stopped or Restart the Bezirk
//        dbConnection.getDatabaseConnection().close();
//        //Check if the data is persisted
//        regPersistence = new RegistryStorage(dbConnection, PersistenceConstants.DB_VERSION);
//        proxyPersistence = regPersistence;
//        proxyRegistry = proxyPersistence.loadBezirkProxyRegistry();
//
//        assertNull(proxyRegistry.getBezirkServiceId("zirk-A")); // should fail
//        assertEquals(proxyRegistry.getBezirkServiceId("zirk-B"), "zid-2");
//        assertNull(proxyRegistry.getBezirkServiceId("zirk-X")); // should fail
//
//        proxyRegistry.deleteBezirkZirkId("zirk-B");
//        proxyPersistence.persistBezirkProxyRegistry();
//
//        //close the db == Bezirk is stopped or Restart the Bezirk
//        dbConnection.getDatabaseConnection().close();
//
//        regPersistence = new RegistryStorage(dbConnection, PersistenceConstants.DB_VERSION);
//        proxyPersistence = regPersistence;
//        proxyRegistry = proxyPersistence.loadBezirkProxyRegistry();
//
//        assertNull(proxyRegistry.getBezirkServiceId("zirk-A")); // should fail
//        assertNull(proxyRegistry.getBezirkServiceId("zirk-B")); // should fail
//        assertNull(proxyRegistry.getBezirkServiceId("zirk-X")); // should fail
//    }
//
//    /**
//     * Check the ServiceIdPersistence. Get the ProxyRegistry and update the BezirkProxyRegisty with sample
//     * ServiceIds. Then close the dbConnection that stimulates restarting or shutting of Bezirk. Check the
//     * same and verify.
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testForBezirkServiceIdPersistenceUnRegistrationWithoutBezirkStartUp() throws Exception {
//        RegistryStorage regPersistence = new RegistryStorage(dbConnection, PersistenceConstants.DB_VERSION);
//        assertNotNull(regPersistence);
//        ProxyPersistence proxyPersistence = regPersistence;
//
//        ProxyRegistry proxyRegistry = proxyPersistence.loadBezirkProxyRegistry();
//        assertNotNull(proxyRegistry);
//        assertNull(proxyRegistry.getBezirkServiceId("null"));
//        // update the registry
//        proxyRegistry.updateBezirkZirkId("zirk-A", "zid-1");
//        proxyPersistence.persistBezirkProxyRegistry();
//        proxyRegistry.updateBezirkZirkId("zirk-B", "zid-2");
//        proxyPersistence.persistBezirkProxyRegistry();
//
//        proxyRegistry.deleteBezirkZirkId("zirk-A");
//        proxyRegistry.deleteBezirkZirkId("zirk-B");
//
//        proxyPersistence.persistBezirkProxyRegistry();
//
//        assertNull(proxyRegistry.getBezirkServiceId("zirk-A")); // should fail
//        assertNull(proxyRegistry.getBezirkServiceId("zirk-B")); // should fail
//        assertNull(proxyRegistry.getBezirkServiceId("zirk-X")); // should fail
//
//    }
//
//}
