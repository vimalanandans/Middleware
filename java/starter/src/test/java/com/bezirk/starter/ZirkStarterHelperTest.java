package com.bezirk.starter;

import com.bezirk.comms.IUhuComms;
import com.bezirk.comms.IUhuCommsLegacy;
import com.bezirk.device.UhuDevice;
import com.bezirk.device.UhuDeviceType;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.sphere.api.IUhuSphereAPI;
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
     * UhuSphere should be non null after successful invocation of initSphere in MainService.
     */
    private void testInitSphere() {

        com.bezirk.starter.ServiceStarterHelper helper = new com.bezirk.starter.ServiceStarterHelper();
        UPADeviceInterface uhuDevice = mockSetUP.getUpaDevice();
        RegistryPersistence registryPersistence = mockSetUP.getRegistryPersistence();
        IUhuComms commsLegacy = Mockito.mock(IUhuCommsLegacy.class);
        IUhuSphereAPI uhuSphere = helper.initSphere(uhuDevice, registryPersistence, commsLegacy);

        assertNotNull("UhuSphere is not initialized. ", uhuSphere);

    }

    /**
     * Positive Testcase :
     * <p/>
     * This covers two scenarios :
     * <p/>
     * a) UhuConfig Display Enabled
     * <p/>
     * In this case the uhudevice type should be set as PC upon device configuration.
     * <p/>
     * b) UhuConfig Display disabled
     * <p/>
     * In this case the uhudevice type should be set as EMBEDDED_KIT upon device configuration.
     */
    private void testConfigureUhuDevice() {
        com.bezirk.starter.ServiceStarterHelper helper = new com.bezirk.starter.ServiceStarterHelper();
        com.bezirk.starter.UhuConfig uhuConfig = new UhuConfig();

        UhuDevice uhuDevice = helper.configureUhuDevice(uhuConfig);

        assertNotNull("UhuDevice is null after configuragtion. ", uhuDevice);

        assertNotNull("UhuDeviceType is null after configuragtion. ", uhuDevice.getDeviceType());

        assertEquals("Uhu Device Type is not configured to PC when display is enabled.", UhuDeviceType.UHU_DEVICE_TYPE_PC, uhuDevice.getDeviceType());

        assertNotNull("UhuDeviceLocation is null after configuragtion. ", uhuDevice.getDeviceLocation());


        uhuConfig.setDisplayEnable("false");

        uhuDevice = helper.configureUhuDevice(uhuConfig);

        assertEquals("Uhu Device Type is not configured to EMBEDDED KIT when display is disabled.", UhuDeviceType.UHU_DEVICE_TYPE_EMBEDDED_KIT, uhuDevice.getDeviceType());
    }

}
