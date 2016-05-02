/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.device;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.impl.DeviceInformation;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;

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
public class ContainsDevice {
    private static final Logger logger = LoggerFactory.getLogger(ContainsDevice.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:ContainsDevice TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereRegistryWrapper:ContainsDevice TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#containsDevice(String)}.
     * <p>
     * Test the behavior of containsDevice when valid deviceId is passed.
     * containsDevice should return <code>true</code>
     * </p>
     */
    @Test
    public final void validDeviceIdShouldReturnTrue() {
        String deviceId = UUID.randomUUID().toString();
        registry.devices.put(deviceId, new DeviceInformation());
        assertTrue(sphereRegistryWrapper.containsDevice(deviceId));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#containsDevice(String)}.
     * <p>
     * Test the behavior of <code>containsDevice</code> when null is passed.
     * containsDevice should return <code>false</code>
     * </p>
     */
    @Test
    public final void nullDeviceIdShouldReturnFalse() {
        String deviceId = null;
        assertFalse(sphereRegistryWrapper.containsDevice(deviceId));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#containsDevice(String)}.
     * <p>
     * Test the behavior of containsDevice when wrong device id is passed.
     * containsDevice should return False
     * </p>
     */
    @Test
    public final void wrongDeviceIdShouldReturnFalse() {
        String deviceId = UUID.randomUUID().toString();
        registry.devices.put(deviceId, new DeviceInformation());
        assertFalse(sphereRegistryWrapper.containsDevice("InvalidDeviceId"));
    }


}

