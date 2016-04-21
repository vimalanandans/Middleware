/**
 *
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.device;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.impl.DeviceInformation;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class GetDevice {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(GetDevice.class);
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up SphereRegistryWrapper:GetDevice TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down SphereRegistryWrapper:GetDevice TestCase *****");
        mockSetUp.destroyTestSetUp();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getDevice(String)}.
     * <p/>
     * <br>Test the behavior of getDevice when valid deviceId is passed.
     * It should return a Device object associated with the deviceId
     */
    @Test
    public final void validDeviceId() {
        String deviceId = UUID.randomUUID().toString();
        DeviceInformation deviceInformation = new DeviceInformation();
        registry.devices.put(deviceId, deviceInformation);
        assertEquals(sphereRegistryWrapper.getDevice(deviceId), deviceInformation);
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getDevice(String)}.
     * <p/>
     * <br>Test the behavior of getDevice method when null string is passed to it.
     * getDevice method is expected to return null.
     */
    @Test
    public final void nullDeviceIdShouldRetunNull() {
        String deviceId = null;
        assertNull(sphereRegistryWrapper.getDevice(deviceId));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getDevice(String)}.
     * <p/>
     * <br>Test the behavior of getDevice when invalid deviceId is passed.
     * It should return null.
     */
    @Test
    public final void invalidDeviceIdReturnsNull() {
        String deviceId = UUID.randomUUID().toString();
        assertNull(sphereRegistryWrapper.getDevice(deviceId));
    }


}

