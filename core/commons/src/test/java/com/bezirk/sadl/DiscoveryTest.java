package com.bezirk.sadl;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This Testcase consists of 3 tests to verify the behavior of discovery zirk in the
 * following scenarios.
 * <p/>
 * a) 	Discovery with no protocolRole
 * b)  Discovery with specific protocolRole and location
 * c)  Discovery with no specific location.
 *
 * @author AJC6KOR
 */
public class DiscoveryTest {

    private final static Logger log = LoggerFactory
            .getLogger(DiscoveryTest.class);
    private static final MockProtocols mockService = new MockProtocols();
    private static final ProtocolRole streamlessPRole = mockService.new StreamlessProtocol();
    private static final SubscribedRole subscribedStreamlessPRole = new SubscribedRole(streamlessPRole);
    private static final MockSetUpUtility mockUtility = new MockSetUpUtility();
    private static BezirkZirkId uhuServiceAId = new BezirkZirkId("ServiceA"), uhuServiceBId = new BezirkZirkId("ServiceB");
    private static Location reception = new Location("OFFICE1", "BLOCK1", "RECEPTION");
    private static UhuSadlManager uhuSadlManager = null;
    private Set<BezirkDiscoveredZirk> discoveredServiceSet;

    private Set<BezirkZirkId> uhuServiceIdSet;

    private BezirkZirkId dServiceId;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("############# Setting up DiscoveryTest TestCase ################");

        mockUtility.setUPTestEnv();
        uhuSadlManager = mockUtility.uhuSadlManager;

    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("############ Shutting down DiscoveryTest Testcase ############");
        mockUtility.destroyTestSetUp();

    }

    @Test
    public void testDiscoverServices() {

		/*Test1 : SadlManager should return null discoveredServiceSet when protocolRole is null.*/
        discoveredServiceSet = uhuSadlManager.discoverZirks(null, reception);
        assertNull("DiscoveredZirk set is not null when services not available.", discoveredServiceSet);

		/*No services are subscribed to streamlessProtocol yet. 
		 * DiscoveredServiceSet should be null.
		 * */
        discoveredServiceSet = uhuSadlManager.discoverZirks(subscribedStreamlessPRole, reception);
        assertNull("DiscoveredZirk set is not null when services not available.", discoveredServiceSet);
		
		
		/* ServiceA and ServiceB are registered and subscribed to StreamlessProtocolRole.
		 * ServiceB has its location set to null */
        uhuSadlManager.registerService(uhuServiceAId);
        uhuSadlManager.subscribeService(uhuServiceAId, subscribedStreamlessPRole);
        uhuSadlManager.registerService(uhuServiceBId);
        uhuSadlManager.subscribeService(uhuServiceBId, subscribedStreamlessPRole);
        uhuSadlManager.setLocation(uhuServiceBId, new Location(null, null, null));

        testDiscoveryWithSpecificLocation();
        testDiscoveryWithNullLocation();
    }


    /*Test2 : SadlManager is queried to discover services subscribed to StreamlessProtocolRole
     * 		  with specific location.
     */
    private void testDiscoveryWithSpecificLocation() {
		
		/*SadlManager is queried to discover services subscribed to StreamlessProtocolRole 
		 * near to reception.SadlManager should return null discoveredServiceSet */
        discoveredServiceSet = uhuSadlManager.discoverZirks(subscribedStreamlessPRole,
                reception);
        assertNull("DiscoveredZirk set is not null when services not available.", discoveredServiceSet);
		
		/*ServiceA is set to the location "OFFICE1/BLOCK1/RECEPTION"*/
        uhuSadlManager.setLocation(uhuServiceAId, reception);
		
		/*	SadlManager should return serviceA for the discovery now. */
        uhuServiceIdSet = discoverServicesUsingProtocolAndLocation(subscribedStreamlessPRole,
                reception);
        assertNotNull("No services found in discovery.", uhuServiceIdSet);
        assertTrue("ServiceA was not discovered when queried for reception as location.", uhuServiceIdSet.contains(uhuServiceAId));
    }

    /*Test3 : SadlManager is queried to discover the services subscribed to StreamlessProtocolRole.
     * 		  As there is no location mentioned in the discovery request it should return
     *        both ServiceA and ServiceB.
     */
    private void testDiscoveryWithNullLocation() {

        uhuServiceIdSet = discoverServicesUsingProtocolAndLocation(subscribedStreamlessPRole,
                null);
        assertNotNull("No services found in discovery.", uhuServiceIdSet);
        assertTrue("ServiceA was not discovered when no location in request.", uhuServiceIdSet.contains(uhuServiceAId));
        assertTrue("ServiceB was not discovered when no location in request.", uhuServiceIdSet.contains(uhuServiceBId));
    }

    private Set<BezirkZirkId> discoverServicesUsingProtocolAndLocation(
            ProtocolRole protocolRole, Location loc) {
        discoveredServiceSet = uhuSadlManager.discoverZirks(protocolRole, loc);
        uhuServiceIdSet = getUhuServiceIdSetOfDiscoveredServices(discoveredServiceSet);
        return uhuServiceIdSet;
    }

    private Set<BezirkZirkId> getUhuServiceIdSetOfDiscoveredServices(
            Set<BezirkDiscoveredZirk> discoveredServiceSet) {
        BezirkZirkEndPoint serviceEndPoint;
        uhuServiceIdSet = null;

        if (discoveredServiceSet != null) {
            uhuServiceIdSet = new HashSet<>();

            for (BezirkDiscoveredZirk discoveredService : discoveredServiceSet) {
                serviceEndPoint = (BezirkZirkEndPoint) (discoveredService
                        .getZirkEndPoint());
                dServiceId = serviceEndPoint.getBezirkZirkId();
                uhuServiceIdSet.add(dServiceId);

            }
        }
        return uhuServiceIdSet;
    }


}
