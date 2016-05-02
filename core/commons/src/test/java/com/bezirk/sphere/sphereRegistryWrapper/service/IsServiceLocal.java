/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.sphere.impl.OwnerZirk;
import com.bezirk.sphere.impl.Zirk;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class IsServiceLocal {
    private static final Logger logger = LoggerFactory.getLogger(IsServiceLocal.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:IsServiceLocal TestCase *****");
        mockSetUp.setUPTestEnv();
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereRegistryWrapper:IsServiceLocal TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#isServiceLocal(String)}.
     * <p>
     * When valid deviceId is passed to isServiceLocal, it should return <code>true</code>
     * </p>
     */
    @Test
    public final void validDeviceIdReturnsTrue() {
        String sphereId = "Home";
        HashSet<String> sphereSet = new HashSet<>();
        sphereSet.add(sphereId);
        String deviceId = mockSetUp.upaDevice.getDeviceId();
        Zirk zirk = new OwnerZirk("ServiceA", deviceId, sphereSet);
        assertTrue(sphereRegistryWrapper.isServiceLocal(zirk.getOwnerDeviceId()));
    }


    /**
     * Test method for {@link SphereRegistryWrapper#isServiceLocal(String)}.
     * <p>
     * When invalid deviceId is passed to isServiceLocal, it should return <code>false</code>
     * </p>
     */
    @Test
    public final void invalidDeviceIdReturnsFalse() {
        // The correct device id is mockSetUp.upaDevice.getDeviceId(), but we are passing an invalid device id here.
        assertFalse(sphereRegistryWrapper.isServiceLocal("InvalidDeviceId"));
    }
}
