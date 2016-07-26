package com.bezirk.starter;


import com.bezirk.comms.Comms;
import com.bezirk.datastorage.RegistryStorage;
import com.bezirk.device.Device;
import com.bezirk.sphere.api.SphereAPI;
import com.bezirk.util.MockSetUpUtilityForBezirkPC;

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

        com.bezirk.starter.ServiceStarterHelper helper = new com.bezirk.starter.ServiceStarterHelper();
        Device bezirkDevice = mockSetUP.getUpaDevice();
        RegistryStorage registryPersistence = mockSetUP.getRegistryPersistence();
        Comms commsLegacy = Mockito.mock(Comms.class);
        SphereAPI bezirkSphere = helper.initSphere(bezirkDevice, registryPersistence, commsLegacy);

        assertNotNull("SphereServiceManager is not initialized. ", bezirkSphere);
    }

}
