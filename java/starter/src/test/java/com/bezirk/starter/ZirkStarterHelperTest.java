package com.bezirk.starter;

import com.bezirk.comms.BezirkCommsLegacy;
import com.bezirk.comms.BezirkComms;
import com.bezirk.device.BezirkDevice;
import com.bezirk.device.BezirkDeviceType;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.util.MockSetUpUtilityForUhuPC;

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

    private static final MockSetUpUtilityForUhuPC mockSetUP = new MockSetUpUtilityForUhuPC();

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

        testConfigureUhuDevice();


    }

    /**
     * Positive Testcase :
     * <p/>
     * BezirkSphere should be non null after successful invocation of initSphere in MainService.
     */
    private void testInitSphere() {

        com.bezirk.starter.ServiceStarterHelper helper = new com.bezirk.starter.ServiceStarterHelper();
        UPADeviceInterface uhuDevice = mockSetUP.getUpaDevice();
        RegistryPersistence registryPersistence = mockSetUP.getRegistryPersistence();
        BezirkComms commsLegacy = Mockito.mock(BezirkCommsLegacy.class);
        BezirkSphereAPI uhuSphere = helper.initSphere(uhuDevice, registryPersistence, commsLegacy);

        assertNotNull("BezirkSphere is not initialized. ", uhuSphere);

    }

    /**
     * Positive Testcase :
     * <p/>
     * This covers two scenarios :
     * <p/>
     * a) BezirkConfig Display Enabled
     * <p/>
     * In this case the uhudevice type should be set as PC upon device configuration.
     * <p/>
     * b) BezirkConfig Display disabled
     * <p/>
     * In this case the uhudevice type should be set as EMBEDDED_KIT upon device configuration.
     */
    private void testConfigureUhuDevice() {
        com.bezirk.starter.ServiceStarterHelper helper = new com.bezirk.starter.ServiceStarterHelper();
        BezirkConfig bezirkConfig = new BezirkConfig();

        BezirkDevice bezirkDevice = helper.configureUhuDevice(bezirkConfig);

        assertNotNull("BezirkDevice is null after configuragtion. ", bezirkDevice);

        assertNotNull("BezirkDeviceType is null after configuragtion. ", bezirkDevice.getDeviceType());

        assertEquals("Bezirk Device Type is not configured to PC when display is enabled.", BezirkDeviceType.BEZIRK_DEVICE_TYPE_PC, bezirkDevice.getDeviceType());

        assertNotNull("UhuDeviceLocation is null after configuragtion. ", bezirkDevice.getDeviceLocation());


        bezirkConfig.setDisplayEnable("false");

        bezirkDevice = helper.configureUhuDevice(bezirkConfig);

        assertEquals("Bezirk Device Type is not configured to EMBEDDED KIT when display is disabled.", BezirkDeviceType.BEZIRK_DEVICE_TYPE_EMBEDDED_KIT, bezirkDevice.getDeviceType());
    }

}
