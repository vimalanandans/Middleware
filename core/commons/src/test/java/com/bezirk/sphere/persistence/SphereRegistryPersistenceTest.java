package com.bezirk.sphere.persistence;

import com.bezirk.persistence.DBConstants;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.bezirk.persistence.IDatabaseConnection;
import com.bezirk.persistence.ISpherePersistence;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.persistence.UhuRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sphere.impl.DeviceInformation;
import com.bezirk.sphere.impl.MemberZirk;
import com.bezirk.sphere.impl.MemberSphere;
import com.bezirk.sphere.impl.OwnerZirk;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.security.SphereKeys;
import com.j256.ormlite.table.TableUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * this JUnit to test the Persistence of SphereRegistry
 * - Vimal
 * Modified by Vijet
 */
public class SphereRegistryPersistenceTest {
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
     * This test case tests the following.
     * 1. open a connection
     * 2. load the sadl Registry
     * 3. update the sadl registry
     * 4. persist the sadl registry
     * 5. close the connection
     * 6. open a connection
     * 7. load the sadl registry and check if its restored to the previous one
     * <SUCCESS>
     */
    @Test
    public void sphereRegistryIntegrationTest() {
        String DBVersion = DBConstants.DB_VERSION;
        try {
            IDatabaseConnection dbConnection = new DatabaseConnectionForJava(DBPath);
            RegistryPersistence regPersistence = new RegistryPersistence(dbConnection, DBVersion);

            ISpherePersistence spherePersistence = regPersistence;
            SphereRegistry sphereRegistry = spherePersistence.loadSphereRegistry();

            updateSphereData(sphereRegistry);
            spherePersistence.persistSphereRegistry();

            //close the connection;
            dbConnection.getDatabaseConnection().close();
            //Assume the application is closed and is restarted
            IDatabaseConnection tempDBConnection = new DatabaseConnectionForJava(DBPath);
            RegistryPersistence tempRegPersistence = new RegistryPersistence(tempDBConnection, DBVersion);
            ISpherePersistence tempSpherePersistence = (ISpherePersistence) tempRegPersistence;
            //load the registry
            SphereRegistry retrievedRegistry = tempSpherePersistence.loadSphereRegistry();
            SphereRegistry storedRegistry = new SphereRegistry();
            updateSphereData(storedRegistry);
            assertEquals(retrievedRegistry, storedRegistry);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    private void updateSphereData(SphereRegistry sphereRegistry) {
        /* STORING SPHERES***/


        /************************** NORMAL SPHERES **************************************************** */
		
				/*create SPhere1, add it to spheres*/
        Sphere homeSphere = new OwnerSphere();
        homeSphere.setSphereName("Home-sphere");
        homeSphere.setSphereType("MOBILE");

        LinkedHashMap<String, ArrayList<BezirkZirkId>> deviceServices = new LinkedHashMap<String, ArrayList<BezirkZirkId>>();
        ArrayList<BezirkZirkId> deviceList = new ArrayList<BezirkZirkId>();
        deviceList.add(new BezirkZirkId("device-id-1-sid-1"));
        deviceList.add(new BezirkZirkId("device-id-1-sid-2"));

        deviceServices.put("device-id-1", deviceList);

        ArrayList<BezirkZirkId> deviceList1 = new ArrayList<BezirkZirkId>();
        deviceList1.add(new BezirkZirkId("device-id-2-sid-1"));
        deviceList1.add(new BezirkZirkId("device-id-2-sid-2"));

        deviceServices.put("device-id-2", deviceList1);

        homeSphere.setDeviceServices(deviceServices);

        //add it to spheres
        sphereRegistry.spheres.put("sphere_id_home", homeSphere);
        /************************** OWNER SPHERES **************************************************** */

        // create OwnerSphere, add it to spheres
        Sphere ownerSphere = new OwnerSphere();
        ownerSphere.setSphereName("ownerSPhere");
        ownerSphere.setSphereType("PC");
        ownerSphere.setDeviceServices(deviceServices);

        sphereRegistry.spheres.put("owner-sphere", ownerSphere);

        /************************** MEMBER SPHERES **************************************************** */
        // create MemberSphere and add it to spheres

        //MemberSphere(String sphereName, String sphereType,HashSet<String> ownerDevices,
        //LinkedHashMap<String, ArrayList<BezirkZirkId>> deviceServices, boolean temporarySphere


        HashSet<String> ownerDeviceIds = new HashSet<String>();
        ownerDeviceIds.add("dev-2");
        ownerDeviceIds.add("dev-1");

        ArrayList<BezirkZirkId> sids = new ArrayList<BezirkZirkId>();
        sids.add(new BezirkZirkId("sid1"));
        sids.add(new BezirkZirkId("sid2"));
        sids.add(new BezirkZirkId("sid3"));

        LinkedHashMap<String, ArrayList<BezirkZirkId>> deviceServicesMemSphere = new LinkedHashMap<String, ArrayList<BezirkZirkId>>();
        deviceServicesMemSphere.put("sphereId", sids);


        MemberSphere memberSphere = new MemberSphere("memeber-sphere", "washingMachine", ownerDeviceIds, deviceServicesMemSphere, true);
        sphereRegistry.spheres.put("member-sphere", memberSphere);
		
		
		/*STORING SPHERE MEMBERSHIP*/
        HashSet<String> sphereSet = new HashSet<String>();
        sphereSet.add("sphere1");
        sphereSet.add("sphere2");

        MemberZirk memberService = new MemberZirk("owner0id-1", "zirk-1", sphereSet);
        OwnerZirk ownerService = new OwnerZirk("owner0id-1", "zirk-1", sphereSet);

        sphereRegistry.sphereMembership.put("member", memberService);
        sphereRegistry.sphereMembership.put("owner", ownerService);
		
		/*STORING DEvICE INFORMATION*/
        sphereRegistry.devices.put("d-1", new DeviceInformation("d-1", "d-type-1"));
        sphereRegistry.devices.put("d-2", new DeviceInformation("d-2", "d-type-2"));
        sphereRegistry.devices.put("d-3", new DeviceInformation("d-3", "d-type-3"));

		/*STORING SPHERE KEYS*/

        byte[] sphereKey1 = new String("sphere-key-1").getBytes();
        byte[] sphereKey2 = new String("sphere-key-2").getBytes();

        byte[] sphereKeyValue1 = new String("SOME DUMMY VALUE").getBytes();
        byte[] sphereKeyValue2 = new String("Some DIFFERENT DUMMY VALUE").getBytes();

        sphereRegistry.sphereKeyMap.put("sphere-key-1", new SphereKeys(sphereKey1, sphereKeyValue1));
        sphereRegistry.sphereKeyMap.put("sphere-key-2", new SphereKeys(sphereKey2, sphereKeyValue2));

    }
}
