package com.bezirk.proxy.pc;

import com.bezirk.discovery.DiscoveryLabel;
import com.bezirk.discovery.DiscoveryProcessor;
import com.bezirk.discovery.DiscoveryRecord;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sadl.BezirkSadlManager;
import com.bezirk.util.MockProtocolsForBezirkPC;
import com.bezirk.util.MockSetUpUtilityForBezirkPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ProxyForServicesTest {
    private static final Logger logger = LoggerFactory.getLogger(ProxyForServicesTest.class);

    private static final MockSetUpUtilityForBezirkPC mockSetUP = new MockSetUpUtilityForBezirkPC();
    private static BezirkSadlManager sadlManager;
    private final String zirkName = "MockZirkA";
    private final String zirkAId = "MockZirkAId";
    private final ZirkId bezirkZirkAId = new ZirkId(zirkAId);
    private final MockProtocolsForBezirkPC.DummyProtocol dummyProtocol = new MockProtocolsForBezirkPC().new DummyProtocol();
    private final SubscribedRole pRole = new SubscribedRole(dummyProtocol);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("********** Setting up ProxyForServicesTest Testcase **********");

        mockSetUP.setUPTestEnv();

        try {
            sadlManager = mockSetUP.getBezirkSadlManager();
        } catch (UnknownHostException e) {
            fail("Unable to set up test environment.");
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("********** Shutting down ProxyForServicesTest Testcase **********");

        mockSetUP.destroyTestSetUp();
    }

    @Test
    public void testRegisterService() {
        ProxyForServices proxyForServices = new ProxyForServices();
        proxyForServices.setSadlRegistry(sadlManager);

        proxyForServices.registerService(bezirkZirkAId, zirkName);

        assertTrue("Proxy is unable to register zirk. ",
                sadlManager.isServiceRegistered(bezirkZirkAId));

        proxyForServices.unregister(bezirkZirkAId);
    }

    @Test
    public void testSubscribeService() {
        ProxyForServices proxyForServices = new ProxyForServices();
        proxyForServices.setSadlRegistry(sadlManager);

        proxyForServices.registerService(bezirkZirkAId, zirkName);
        proxyForServices.subscribeService(bezirkZirkAId, pRole);

        assertTrue(
                "Proxy is allowing duplicate subscription. ",
                sadlManager.isStreamTopicRegistered(
                        dummyProtocol.getStreamTopics()[0], bezirkZirkAId));

        proxyForServices.unregister(bezirkZirkAId);

    }

    @Test
    public void testUnregisterService() {
        ProxyForServices proxyForServices = new ProxyForServices();
        proxyForServices.setSadlRegistry(sadlManager);

        proxyForServices.registerService(bezirkZirkAId, zirkName);

        proxyForServices.unregister(bezirkZirkAId);

        assertFalse("Proxy is unable to perform unregistration ",
                sadlManager.isServiceRegistered(bezirkZirkAId));
    }

    @Test
    public void testUnsubscribeService() {
        ProxyForServices proxyForServices = new ProxyForServices();
        proxyForServices.setSadlRegistry(sadlManager);

        proxyForServices.registerService(bezirkZirkAId, zirkName);
        proxyForServices.subscribeService(bezirkZirkAId, pRole);
        proxyForServices.unsubscribe(bezirkZirkAId, pRole);
        assertFalse(
                "Proxy is unable to perform unsubscription. ",
                sadlManager.isStreamTopicRegistered(
                        dummyProtocol.getStreamTopics()[0], bezirkZirkAId));

        proxyForServices.unregister(bezirkZirkAId);

    }

    @Test
    public void testDiscover() {
        ProxyForServices proxyForServices = new ProxyForServices();
        proxyForServices.setSadlRegistry(sadlManager);

        String serviceId = "ServiceB";
        ZirkId bezirkZirkBId = new ZirkId(serviceId);

        proxyForServices.registerService(bezirkZirkBId, serviceId);
        proxyForServices.subscribeService(bezirkZirkBId, pRole);

        proxyForServices.discover(bezirkZirkAId, null, pRole, 3, 10000, 1);
        ConcurrentHashMap<DiscoveryLabel, DiscoveryRecord> discoveredMap = new ConcurrentHashMap<>();
        try {
            discoveredMap = DiscoveryProcessor.getDiscovery()
                    .getDiscoveredMap();
        } catch (InterruptedException e) {

            fail("Unable to fetch discovered map. " + e.getMessage());
        }
        assertEquals("Proxy is unable to perform discovery. ",
                discoveredMap.size(), 1);

        DiscoveryLabel discLabel = discoveredMap.entrySet().iterator().next()
                .getKey();
        assertEquals(
                "DiscoveryId is not matching with the id in the discovery request.",
                discLabel.getDiscoveryId(), 3);

        proxyForServices.unregister(bezirkZirkBId);
    }

    @Test
    public void testSetLocation() {
        ProxyForServices proxyForServices = new ProxyForServices();
        proxyForServices.setSadlRegistry(sadlManager);
        proxyForServices.registerService(bezirkZirkAId, zirkName);

        Location location = new Location("OFFICE1/BLOCK1/FLOOR1");
        proxyForServices.setLocation(bezirkZirkAId, location);

        Location locInRegistry = ((BezirkSadlManager) proxyForServices
                .getSadlRegistry()).getLocationForService(bezirkZirkAId);
        assertEquals(
                "Location for mockservice is not matching the location set via proxy.",
                location, locInRegistry);
    }
}
