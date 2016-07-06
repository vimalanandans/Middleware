/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.testUtilities.Device;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.testUtilities.SphereTestUtility;

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
public class RegisterService {
    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:RegisterService TestCase *****");
        mockSetUp.setUPTestEnv();
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereRegistryWrapper:RegisterService TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#registerService(ZirkId, String)}.
     * <p>
     * When valid ServiceId and a new Zirk name is passed to registerZirk,
     * it should add the zirk membership to the registry and return <code>true</code>.
     * </p>
     */
    @Test
    public final void validServiceIdAndNewServiceNameReturnsTrue() {
        String serviceId = UUID.randomUUID().toString();
        ZirkId zirkId = new ZirkId(serviceId);
        String serviceName = sphereTestUtility.OWNER_ZIRK_NAME_1;
        assertTrue(sphereRegistryWrapper.registerService(zirkId, serviceName));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#registerService(ZirkId, String)}.
     * <p>
     * When valid ServiceId and a new Zirk name is passed to registerZirk,
     * it should update the existing zirk with the name passed and return <code>true</code>.
     * </p>
     */
    @Test
    public final void newServiceNameForExistingServiceReturnsTrue() {
        String serviceId = UUID.randomUUID().toString();
        ZirkId zirkId = new ZirkId(serviceId);
        String serviceName = sphereTestUtility.OWNER_ZIRK_NAME_2;
        sphereRegistryWrapper.addMembership(zirkId, sphereRegistryWrapper.getDefaultSphereId(), Device.DEVICE_ID, serviceName);

        // When a new zirk name "newName" is passed for the same zirk Id, registerZirk method must return true.
        assertTrue(sphereRegistryWrapper.registerService(zirkId, "newName"));
    }

}
