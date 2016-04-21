/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 */
package com.bezirk.persistence;

import com.j256.ormlite.table.TableUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * This class test the Persistence of the UhuServiceIdPersistence, to check loading and storing of
 * UhuServiceId when the service/s register/s and Unregister/s.
 */
public class UhuServiceIdPersistenceTest {
    String DBPath = "./";
    IDatabaseConnection dbConnection = null;

    @Before
    public void before() throws IOException {
        dbConnection = new DatabaseConnectionForJava(DBPath);
    }

    @After
    public void tearDown() throws NullPointerException, SQLException, Exception {
        // Deleting the uhu_database.sqlite is not happening so after each test, I am dropping the table
        TableUtils.dropTable(dbConnection.getDatabaseConnection(), UhuRegistry.class, true);
    }

    /**
     * Checks if the ServiceIds are persisting in the ProxyPersistence. Get hold of the ProxyPersistence,
     * Update the ProxyPersistence with sample serviceIds and persist. Shut the Bezirk or Restart the Bezirk
     * and load the ProxyPersistence. Check if the sample serviceIds exist in the ProxyPersistence, that
     * validates this testcase.
     *
     * @throws Exception
     */
    @Test
    public void testForUhuServiceIdPersistenceRegistration() throws Exception {
        RegistryPersistence regPersistence = new RegistryPersistence(dbConnection, DBConstants.DB_VERSION);
        assertNotNull(regPersistence);
        IUhuProxyPersistence proxyPersistence = (IUhuProxyPersistence) regPersistence;

        UhuProxyRegistry proxyRegistry = proxyPersistence.loadUhuProxyRegistry();
        assertNotNull(proxyRegistry);
        assertNull(proxyRegistry.getUhuServiceId("null"));
        // update the registry
        proxyRegistry.updateUhuServiceId("service-A", "sid-1");
        proxyPersistence.persistUhuProxyRegistry();
        proxyRegistry.updateUhuServiceId("service-B", "sid-2");
        proxyPersistence.persistUhuProxyRegistry();

        //close the db == Uhu is stopped or Restart the Uhu
        dbConnection.getDatabaseConnection().close();
        proxyPersistence = null;
        proxyRegistry = null;
        regPersistence = null;
        //Check if the data is persisted
        regPersistence = new RegistryPersistence(dbConnection, DBConstants.DB_VERSION);
        proxyPersistence = (IUhuProxyPersistence) regPersistence;
        proxyRegistry = proxyPersistence.loadUhuProxyRegistry();

        assertEquals(proxyRegistry.getUhuServiceId("service-A"), "sid-1");
        assertEquals(proxyRegistry.getUhuServiceId("service-B"), "sid-2");
        assertNull(proxyRegistry.getUhuServiceId("service-X")); // should fail

    }

    /**
     * Check the ServiceIdPersistence. Get the UhuProxyRegistry and update the UhuProxyRegisty with sample
     * ServiceIds. Then close the dbConnection that stimulates restarting or shutting of Bezirk. Delete the
     * sample service Ids and shut the Bezirk. Check the registry for map that contains no serviceId that
     * validates this test case.
     *
     * @throws Exception
     */
    @Test
    public void testForUhuServiceIdPersistenceUnRegistrationWithBezirkStartUp() throws Exception {
        RegistryPersistence regPersistence = new RegistryPersistence(dbConnection, DBConstants.DB_VERSION);
        assertNotNull(regPersistence);
        IUhuProxyPersistence proxyPersistence = (IUhuProxyPersistence) regPersistence;

        UhuProxyRegistry proxyRegistry = proxyPersistence.loadUhuProxyRegistry();
        assertNotNull(proxyRegistry);
        assertNull(proxyRegistry.getUhuServiceId("null"));
        // update the registry
        proxyRegistry.updateUhuServiceId("service-A", "sid-1");
        proxyPersistence.persistUhuProxyRegistry();
        proxyRegistry.updateUhuServiceId("service-B", "sid-2");
        proxyPersistence.persistUhuProxyRegistry();

        proxyRegistry.deleteUhuServiceId("service-A");
        proxyPersistence.persistUhuProxyRegistry();

        //close the db == Uhu is stopped or Restart the Uhu
        dbConnection.getDatabaseConnection().close();
        proxyPersistence = null;
        proxyRegistry = null;
        regPersistence = null;
        //Check if the data is persisted
        regPersistence = new RegistryPersistence(dbConnection, DBConstants.DB_VERSION);
        proxyPersistence = (IUhuProxyPersistence) regPersistence;
        proxyRegistry = proxyPersistence.loadUhuProxyRegistry();

        assertNull(proxyRegistry.getUhuServiceId("service-A")); // should fail
        assertEquals(proxyRegistry.getUhuServiceId("service-B"), "sid-2");
        assertNull(proxyRegistry.getUhuServiceId("service-X")); // should fail

        proxyRegistry.deleteUhuServiceId("service-B");
        proxyPersistence.persistUhuProxyRegistry();

        //close the db == Uhu is stopped or Restart the Uhu
        dbConnection.getDatabaseConnection().close();
        proxyPersistence = null;
        proxyRegistry = null;
        regPersistence = null;

        regPersistence = new RegistryPersistence(dbConnection, DBConstants.DB_VERSION);
        proxyPersistence = (IUhuProxyPersistence) regPersistence;
        proxyRegistry = proxyPersistence.loadUhuProxyRegistry();

        assertNull(proxyRegistry.getUhuServiceId("service-A")); // should fail
        assertNull(proxyRegistry.getUhuServiceId("service-B")); // should fail
        assertNull(proxyRegistry.getUhuServiceId("service-X")); // should fail

    }

    /**
     * Check the ServiceIdPersistence. Get the UhuProxyRegistry and update the UhuProxyRegisty with sample
     * ServiceIds. Then close the dbConnection that stimulates restarting or shutting of Bezirk. Check the
     * same and verify.
     *
     * @throws Exception
     */
    @Test
    public void testForUhuServiceIdPersistenceUnRegistrationWithoutBezirkStartUp() throws Exception {
        RegistryPersistence regPersistence = new RegistryPersistence(dbConnection, DBConstants.DB_VERSION);
        assertNotNull(regPersistence);
        IUhuProxyPersistence proxyPersistence = (IUhuProxyPersistence) regPersistence;

        UhuProxyRegistry proxyRegistry = proxyPersistence.loadUhuProxyRegistry();
        assertNotNull(proxyRegistry);
        assertNull(proxyRegistry.getUhuServiceId("null"));
        // update the registry
        proxyRegistry.updateUhuServiceId("service-A", "sid-1");
        proxyPersistence.persistUhuProxyRegistry();
        proxyRegistry.updateUhuServiceId("service-B", "sid-2");
        proxyPersistence.persistUhuProxyRegistry();

        proxyRegistry.deleteUhuServiceId("service-A");
        proxyRegistry.deleteUhuServiceId("service-B");

        proxyPersistence.persistUhuProxyRegistry();

        assertNull(proxyRegistry.getUhuServiceId("service-A")); // should fail
        assertNull(proxyRegistry.getUhuServiceId("service-B")); // should fail
        assertNull(proxyRegistry.getUhuServiceId("service-X")); // should fail

    }

}
