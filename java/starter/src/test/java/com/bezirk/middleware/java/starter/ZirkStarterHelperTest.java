package com.bezirk.middleware.java.starter;


import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.datastorage.RegistryStorage;
import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.java.networking.JavaNetworkManager;
import com.bezirk.middleware.core.sphere.api.SphereAPI;
import com.bezirk.middleware.java.util.MockSetUpUtilityForBezirkPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;

/**
 * This testcase verifies the methods in ServiceStarterHelper class.
 *
 * @author AJC6KOR
 */
public class ZirkStarterHelperTest {

    private static final MockSetUpUtilityForBezirkPC mockSetUP = new MockSetUpUtilityForBezirkPC();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        mockSetUP.setUPTestEnv();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        mockSetUP.destroyTestSetUp();

    }

    @Test
    public void test() {
        testInitSphere();
    }

    /**
     * SphereServiceManager should be non null after successful invocation of initSphere in MainService.
     */
    private void testInitSphere() {

        com.bezirk.middleware.java.starter.ServiceStarterHelper helper = new com.bezirk.middleware.java.starter.ServiceStarterHelper();
        Device bezirkDevice = mockSetUP.getUpaDevice();
        RegistryStorage registryPersistence = mockSetUP.getRegistryPersistence();
        Comms commsLegacy = Mockito.mock(Comms.class);
        SphereAPI bezirkSphere = helper.initSphere(bezirkDevice, registryPersistence, commsLegacy, new JavaNetworkManager());

        assertNotNull("SphereServiceManager is not initialized. ", bezirkSphere);
    }

}
