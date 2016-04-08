package com.bosch.upa.uhu.test.sphere.discoveryProcessor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.sphere.impl.DiscoveryProcessor;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockProtocols;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;
import com.bosch.upa.uhu.test.sphere.testUtilities.SphereTestUtility;

/**
 * 
 * @author rishabh
 *
 */
public class DiscoverSphere {

    private static DiscoveryProcessor discoveryProcessor;
    private static final Logger log = LoggerFactory.getLogger(DiscoverSphere.class);
    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final MockProtocols mockProtocols = new MockProtocols();
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {        
        log.info("***** Setting up DiscoverSphere TestCase *****");
        mockSetUp.setUPTestEnv();
        discoveryProcessor = mockSetUp.discoveryProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        mockSetUp.destroyTestSetUp();
        sphereTestUtility = null;
        log.info("***** Shutting down DiscoverSphere TestCase *****");
    }

   
     @Test
     public void testValidSphereDiscovery() {
     String sphereId = sphereTestUtility.generateOwnerCombo();
     assertTrue(discoveryProcessor.discoverSphere(sphereId));
     }

    @Test
    public void testInvalidSphereDiscoverySphereIdNull() {
        assertFalse(discoveryProcessor.discoverSphere(null));
    }
    
    @Test
    public void testInvalidSphereDiscoverySphereIdInvalid() {
        assertFalse(discoveryProcessor.discoverSphere("Random"));
    }

}
