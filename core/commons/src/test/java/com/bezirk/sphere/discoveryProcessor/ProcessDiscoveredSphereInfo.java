package com.bezirk.sphere.discoveryProcessor;

import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.impl.DiscoveryProcessor;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.testUtilities.SphereTestUtility;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the processing of discovered sphere info. sphere
 * Registry is queried to verify the discovered services are added to registry.
 *
 * @author AJC6KOR
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
     * Set has valid device(s) with zirk(s) SphereId is valid
     */
    @Test
    public void testValidSphereInfoSet() {
        String sphereId = sphereTestUtility.generateOwnerCombo();
        int devicesForSphereBefore = SphereRegistryWrapper.getSphere(sphereId).getDeviceServices().size();

        HashSet<BezirkSphereInfo> discoveredSphereInfoSet = new HashSet<BezirkSphereInfo>();
        ArrayList<BezirkDeviceInfo> bezirkDeviceInfos = new ArrayList<BezirkDeviceInfo>();
        bezirkDeviceInfos.add(sphereTestUtility.getBezirkDeviceInfo());
        BezirkSphereInfo bezirkSphereInfo = new BezirkSphereInfo(sphereId, sphereTestUtility.OWNER_SPHERE_NAME_1, null,
                bezirkDeviceInfos, null);
        discoveredSphereInfoSet.add(bezirkSphereInfo);

        discoveryProcessor.processDiscoveredSphereInfo(discoveredSphereInfoSet, sphereId);
        int devicesForSphereAfter = SphereRegistryWrapper.getSphere(sphereId).getDeviceServices().size();
        assertEquals(devicesForSphereBefore + 1, devicesForSphereAfter);

        for (BezirkDeviceInfo deviceInfo : bezirkDeviceInfos) {
            for (BezirkZirkInfo serviceInfo : deviceInfo.getZirkList()) {
                assertTrue("sphere registry is not having discovered services.",
                        registry.sphereMembership.containsKey(serviceInfo.getZirkId()));
            }
        }
    }

}
