/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.proxy.api.impl.UhuZirkId;
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

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(RegisterService.class);
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up SphereRegistryWrapper:RegisterService TestCase *****");
        mockSetUp.setUPTestEnv();
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down SphereRegistryWrapper:RegisterService TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#registerService(UhuZirkId, String)}.
     * <p/>
     * <br>When valid ServiceId and a new Service name is passed to registerZirk,
     * it should add the service membership to the registry and return True.
     */
    @Test
    public final void validServiceIdAndNewServiceNameReturnsTrue() {
        String serviceId = UUID.randomUUID().toString();
        UhuZirkId uhuServiceId = new UhuZirkId(serviceId);
        String serviceName = sphereTestUtility.OWNER_SERVICE_NAME_1;
        assertTrue(sphereRegistryWrapper.registerService(uhuServiceId, serviceName));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#registerService(UhuZirkId, String)}.
     * <p/>
     * <br>When valid ServiceId and a new Service name is passed to registerZirk,
     * it should update the existing service with the name passed and return True.
     */
    @Test
    public final void newServiceNameForExistingServiceReturnsTrue() {
        String serviceId = UUID.randomUUID().toString();
        UhuZirkId uhuServiceId = new UhuZirkId(serviceId);
        String serviceName = sphereTestUtility.OWNER_SERVICE_NAME_2;
        sphereRegistryWrapper.addMembership(uhuServiceId, sphereRegistryWrapper.getDefaultSphereId(), Device.DEVICE_ID, serviceName);

        // When a new service name "newName" is passed for the same service Id, registerZirk method must return true.
        assertTrue(sphereRegistryWrapper.registerService(uhuServiceId, "newName"));
    }

}
