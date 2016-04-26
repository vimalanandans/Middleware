package com.bezirk.sphere.discoveryProcessor;

import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.sphere.impl.DiscoveryProcessor;
import com.bezirk.sphere.testUtilities.MockProtocols;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.testUtilities.SphereTestUtility;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

/**
 * @author rishabh
 */
public class ProcessRequest {

    private static final Logger log = LoggerFactory.getLogger(ProcessRequest.class);
    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final MockProtocols mockProtocols = new MockProtocols();
    private static DiscoveryProcessor discoveryProcessor;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up ProcessRequest TestCase *****");
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
        log.info("***** Shutting down ProcessRequest TestCase *****");
    }

    @Test
    public void testProcessSphereDiscoveryRequest() {
        int discoveryId = 1;
        ProtocolRole protocolRole = mockProtocols.new DummyProtocol();
        SubscribedRole subscribedRole = new SubscribedRole(protocolRole);
        Location location = new Location("OFFICE1", "BLOCK1", "ROOM1");

        UhuServiceEndPoint sender = new UhuServiceEndPoint(sphereTestUtility.OWNER_SERVICE_ID_1);
        sender.device = sphereTestUtility.DEVICE_2.getDeviceId();
        // sender.device = new Device().getDeviceId();
        // String sphereId = createSphereWithDeviceServices();
        String sphereId = sphereTestUtility.generateOwnerCombo();
        long timeout = 10000;
        int maxDiscovered = 1;

        DiscoveryRequest discoveryRequest = new DiscoveryRequest(sphereId, sender, location, subscribedRole,
                discoveryId, timeout, maxDiscovered);

        assertTrue(discoveryProcessor.processRequest(discoveryRequest));
    }

}
