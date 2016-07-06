package com.bezirk.starter;


import com.bezirk.comms.BezirkComms;
import com.bezirk.device.BezirkDevice;
import com.bezirk.device.BezirkDeviceType;
import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.util.MockSetUpUtilityForBezirkPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

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

        testConfigureBezirkDevice();
    }

    /**
     * BezirkSphere should be non null after successful invocation of initSphere in MainService.
     */
    private void testInitSphere() {

        com.bezirk.starter.ServiceStarterHelper helper = new com.bezirk.starter.ServiceStarterHelper();
        BezirkDeviceInterface bezirkDevice = mockSetUP.getUpaDevice();
        RegistryPersistence registryPersistence = mockSetUP.getRegistryPersistence();
        BezirkComms commsLegacy = Mockito.mock(BezirkComms.class);
        BezirkSphereAPI bezirkSphere = helper.initSphere(bezirkDevice, registryPersistence, commsLegacy);

        assertNotNull("BezirkSphere is not initialized. ", bezirkSphere);

    }

    /**
     * Positive Testcase :
     * <p>
     * This covers two scenarios :
     * </p> <p>
     * a) BezirkConfig Display Enabled -- In this case the uhudevice type should be set as PC upon
     * device configuration.
     * </p> <p>
     * b) BezirkConfig Display disabled
     * </p>
     * In this case the BezirkDevice type should be set as EMBEDDED_KIT upon device configuration.
     */
    private void testConfigureBezirkDevice() {
        com.bezirk.starter.ServiceStarterHelper helper = new com.bezirk.starter.ServiceStarterHelper();
        BezirkConfig bezirkConfig = new BezirkConfig();

        BezirkDevice bezirkDevice = helper.configureBezirkDevice(bezirkConfig);

        assertNotNull("BezirkDevice is null after configuration. ", bezirkDevice);

        assertNotNull("BezirkDeviceType is null after configuration. ", bezirkDevice.getDeviceType());

        // commenting as the display enabled logic has been changed to false by default
        //assertEquals("Bezirk Device Type is not configured to PC when display is enabled.", BezirkDeviceType.BEZIRK_DEVICE_TYPE_PC, bezirkDevice.getDeviceType());

        assertNotNull("BezirkDeviceLocation is null after configuration. ", bezirkDevice.getDeviceLocation());


        //bezirkConfig.setDisplayEnable("false");

        bezirkDevice = helper.configureBezirkDevice(bezirkConfig);

        assertEquals("Bezirk Device Type is not configured to EMBEDDED KIT when display is disabled.", BezirkDeviceType.BEZIRK_DEVICE_TYPE_EMBEDDED_KIT, bezirkDevice.getDeviceType());
    }

}
