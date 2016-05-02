package com.bezirk.sadl;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.persistence.DBConstants;
import com.bezirk.persistence.DatabaseConnection;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.bezirk.persistence.SadlPersistence;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.persistence.BezirkRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.j256.ormlite.table.TableUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegistryPersistenceTest {
    String DBPath = "./";
    DatabaseConnection dbConnection = null;

    @Before
    public void before() throws IOException {
        dbConnection = new DatabaseConnectionForJava(DBPath);
    }

    @After
    public void tearDown() throws NullPointerException, SQLException, Exception {
        // Deleting the uhu_database.sqlite is not happening so after each test, I am dropping the table
        TableUtils.dropTable(dbConnection.getDatabaseConnection(), BezirkRegistry.class, true);
    }

    @Test
    public void testRegistryPersistenceForVersionMismatch() throws NullPointerException, SQLException, IOException, Exception {
        String DBVersion = "1.1.1";
        RegistryPersistence regPersistence = new RegistryPersistence(dbConnection, DBVersion);
    }

//	@Test  //Constructor tests - checkDatabase() and super
//	public void testRegistryPersistenceForDefault() throws NullPointerException, SQLException, IOException, Exception{
//		String DBVersion = DBConstants.DB_VERSION;
//		//DatabaseConnection dbConnection = new DatabaseConnectionForJava(DBPath);
//		RegistryPersistence regPersistence = new RegistryPersistence(dbConnection,DBVersion);
//		//table should be created
//		assertTrue(dbConnection.getPersistenceDAO().isTableExists());
//		// Table should be having only one row with empty SphereRegistry and SadlRegistry
//		assertEquals(new SadlRegistry(),regPersistence.loadSadlRegistry());
//		assertEquals(new SphereRegistry(),regPersistence.loadSphereRegistry());
//	}


    @Test
    public void testRegistryPersistenceLoad() {
        String DBVersion = DBConstants.DB_VERSION;
        try {
            DatabaseConnection dbConnection = new DatabaseConnectionForJava(DBPath);
            RegistryPersistence regPersistence = new RegistryPersistence(dbConnection, DBVersion);

            SadlPersistence sadlPersistence = (SadlPersistence) regPersistence;

            SadlRegistry sadlRegistry = sadlPersistence.loadSadlRegistry();

            //sadlRegistry.
            BezirkZirkId sid1 = new BezirkZirkId("temp-uhuservice-id-1");
            BezirkZirkId sid2 = new BezirkZirkId("temp-uhuservice-id-2");
            BezirkZirkId sid3 = new BezirkZirkId("temp-uhuservice-id-3");
            BezirkZirkId sid4 = new BezirkZirkId("temp-uhuservice-id-4");
            BezirkZirkId sid5 = new BezirkZirkId("temp-uhuservice-id-5");


            //sid
            HashSet<BezirkZirkId> sidMap = new HashSet<BezirkZirkId>();
            sidMap.add(sid1);
            sidMap.add(sid2);
            sidMap.add(sid3);
            sidMap.add(sid4);
            sidMap.add(sid5);

            sadlRegistry.sid.add(sid1);
            sadlRegistry.sid.add(sid2);
            sadlRegistry.sid.add(sid3);
            sadlRegistry.sid.add(sid4);
            sadlRegistry.sid.add(sid5);

            // Events
            HashSet<BezirkZirkId> tempSet1 = new HashSet<BezirkZirkId>();
            tempSet1.add(sid1);
            tempSet1.add(sid2);
            sadlRegistry.eventMap.put("topic-1", tempSet1);

            //Location
            sadlRegistry.locationMap.put(sid1, new Location("location-1"));
            sadlRegistry.locationMap.put(sid1, new Location("location-2", "in", "region"));
            sadlRegistry.locationMap.put(sid1, new Location("location-3", "in-22", "region"));

            sadlPersistence.persistSadlRegistry();

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    /**
     * This test case tests the following.
     * 1. open a connection
     * 2. load the sadl Registry
     * 3. update the sadl registry
     * 4. persist the sadl registry
     * 5. close the connection
     * 6. open a connection
     * 7. load the sadl registry and check if its restored to the previous one
     *
     * @throws Exception
     * @throws IOException
     * @throws SQLException
     * @throws NullPointerException
     */
    @Test
    public void sadlPersistenceIntegartionTest() throws NullPointerException, SQLException, IOException, Exception {
        String DBVersion = DBConstants.DB_VERSION;
        RegistryPersistence regPersistence = new RegistryPersistence(dbConnection, DBVersion);
        //give it to sadl
        SadlPersistence sadlPersistence = (SadlPersistence) regPersistence;
        // load the sadl registry
        SadlRegistry aboutToPersist = sadlPersistence.loadSadlRegistry();
        //update
        updateSampleSadlRegistry(aboutToPersist);
        //persist sadl registry
        sadlPersistence.persistSadlRegistry();
        //close the connection;
        dbConnection.getDatabaseConnection().close();
        //Assume the application is closed and is restarted
        DatabaseConnection tempDBConnection = new DatabaseConnectionForJava(DBPath);
        RegistryPersistence tempRegPersistence = new RegistryPersistence(tempDBConnection, DBVersion);
        SadlPersistence tempSadlPersistence = (SadlPersistence) tempRegPersistence;
        //load the registry
        SadlRegistry retrievedRegistry = tempSadlPersistence.loadSadlRegistry();
        SadlRegistry storedRegistry = new SadlRegistry();
        updateSampleSadlRegistry(storedRegistry);
        assertEquals(retrievedRegistry, storedRegistry);
    }

    private void updateSampleSadlRegistry(SadlRegistry tempSadlRegistry) {

        BezirkZirkId sid1 = new BezirkZirkId("temp-uhuservice-id-1");
        BezirkZirkId sid2 = new BezirkZirkId("temp-uhuservice-id-2");
        BezirkZirkId sid3 = new BezirkZirkId("temp-uhuservice-id-3");
        BezirkZirkId sid4 = new BezirkZirkId("temp-uhuservice-id-4");
        BezirkZirkId sid5 = new BezirkZirkId("temp-uhuservice-id-5");


        //sid
        HashSet<BezirkZirkId> sidMap = new HashSet<BezirkZirkId>();
        sidMap.add(sid1);
        sidMap.add(sid2);
        sidMap.add(sid3);
        sidMap.add(sid4);
        sidMap.add(sid5);

        tempSadlRegistry.sid.add(sid1);
        tempSadlRegistry.sid.add(sid2);
        tempSadlRegistry.sid.add(sid3);
        tempSadlRegistry.sid.add(sid4);
        tempSadlRegistry.sid.add(sid5);

        //protocolMap
        ConcurrentHashMap<String, HashSet<BezirkZirkId>> protocolMap = new ConcurrentHashMap<String, HashSet<BezirkZirkId>>();
        protocolMap.put("protocol-1", sidMap);

        tempSadlRegistry.protocolMap.put("protocol-1", sidMap);
        //protocolDescription Map
        ConcurrentHashMap<String, String> protocolDescMap = new ConcurrentHashMap<String, String>();
        protocolDescMap.put("protocol-1", "protocol-desc-1");
        protocolDescMap.put("protocol-2", "protocol-desc-2");
        tempSadlRegistry.protocolDescMap.put("protocol-1", "protocol-desc-1");
        tempSadlRegistry.protocolDescMap.put("protocol-2", "protocol-desc-2");
        //events Map
        HashSet<BezirkZirkId> tempSet1 = new HashSet<BezirkZirkId>();
        tempSet1.add(sid1);
        tempSet1.add(sid2);
        tempSadlRegistry.eventMap.put("topic-1", tempSet1);
        HashSet<BezirkZirkId> tempSet2 = new HashSet<BezirkZirkId>();
        tempSet2.add(sid2);
        tempSet2.add(sid4);
        tempSadlRegistry.eventMap.put("topic-2", tempSet2);
        HashSet<BezirkZirkId> tempSet3 = new HashSet<BezirkZirkId>();
        tempSet3.add(sid1);
        tempSet3.add(sid3);
        tempSadlRegistry.eventMap.put("topic-3", tempSet3);
        //streams Map
        HashSet<BezirkZirkId> tempStreamSet1 = new HashSet<BezirkZirkId>();
        tempStreamSet1.add(sid1);
        tempStreamSet1.add(sid2);
        tempSadlRegistry.streamMap.put("stream-1", tempStreamSet1);

        //Location Map
        tempSadlRegistry.locationMap.put(sid1, new Location("location-1"));
        tempSadlRegistry.locationMap.put(sid2, new Location("location-2", "in", "region"));
        tempSadlRegistry.locationMap.put(sid3, new Location("location-3", "in-22", "region"));


    }
}
