package com.bosch.upa.uhu.test.sphere.discoveryProcessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.api.objects.UhuDeviceInfo;
import com.bosch.upa.uhu.api.objects.UhuServiceInfo;
import com.bosch.upa.uhu.api.objects.UhuSphereInfo;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.sphere.impl.DiscoveryProcessor;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;
import com.bosch.upa.uhu.test.sphere.testUtilities.SphereTestUtility;

/**
 * This testcase verifies the processing of discovered sphere info. Sphere
 * Registry is queried to verify the discovered services are added to registry.
 * 
 * 
 * @author AJC6KOR
 *
 */
public class ProcessDiscoveredSphereInfo {

    private static final Logger log = LoggerFactory.getLogger(ProcessDiscoveredSphereInfo.class);
    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static DiscoveryProcessor discoveryProcessor;
    private static SphereRegistry registry;
    private static SphereTestUtility sphereTestUtility;
    private static SphereRegistryWrapper SphereRegistryWrapper;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up ProcessDiscoveredSphereInfo TestCase *****");
        mockSetUp.setUPTestEnv();
        discoveryProcessor = mockSetUp.discoveryProcessor;
        registry = mockSetUp.registry;
        SphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down ProcessDiscoveredSphereInfo TestCase *****");
        mockSetUp.destroyTestSetUp();
        sphereTestUtility = null;
    }

    /**
     * Test {@link DiscoveryProcessor#processDiscoveredSphereInfo(Set, String)}
     * Set is null SphereId is valid
     */
    @Test(expected = NullPointerException.class)
    public void testNullSphereInfoSet() {
        String sphereId = sphereTestUtility.generateOwnerCombo();
        discoveryProcessor.processDiscoveredSphereInfo(null, sphereId);        
    }
    
    /**
     * Test {@link DiscoveryProcessor#processDiscoveredSphereInfo(Set, String)}
     * Set has valid device(s) with service(s) SphereId is valid
     */
    @Test
    public void testValidSphereInfoSet() {
        String sphereId = sphereTestUtility.generateOwnerCombo();
        int devicesForSphereBefore = SphereRegistryWrapper.getSphere(sphereId).getDeviceServices().size();

        HashSet<UhuSphereInfo> discoveredSphereInfoSet = new HashSet<UhuSphereInfo>();
        ArrayList<UhuDeviceInfo> uhuDeviceInfos = new ArrayList<UhuDeviceInfo>();
        uhuDeviceInfos.add(sphereTestUtility.getUhuDeviceInfo());
        UhuSphereInfo uhuSphereInfo = new UhuSphereInfo(sphereId, sphereTestUtility.OWNER_SPHERE_NAME_1, null,
                uhuDeviceInfos, null);
        discoveredSphereInfoSet.add(uhuSphereInfo);

        discoveryProcessor.processDiscoveredSphereInfo(discoveredSphereInfoSet, sphereId);
        int devicesForSphereAfter = SphereRegistryWrapper.getSphere(sphereId).getDeviceServices().size();
        assertEquals(devicesForSphereBefore + 1, devicesForSphereAfter);

        for (UhuDeviceInfo deviceInfo : uhuDeviceInfos) {
            for (UhuServiceInfo serviceInfo : deviceInfo.getServiceList()) {
                assertTrue("Sphere registry is not having discovered services.",
                        registry.sphereMembership.containsKey(serviceInfo.getServiceId()));
            }
        }
    }

}
