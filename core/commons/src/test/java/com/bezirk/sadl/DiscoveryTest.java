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
    private final static Logger logger = LoggerFactory.getLogger(DiscoveryTest.class);

    private static final MockProtocols mockService = new MockProtocols();
    private static final ProtocolRole streamlessPRole = mockService.new StreamlessProtocol();
    private static final SubscribedRole subscribedStreamlessPRole = new SubscribedRole(streamlessPRole);
    private static final MockSetUpUtility mockUtility = new MockSetUpUtility();
    private static BezirkZirkId bezirkZirkAId = new BezirkZirkId("ServiceA"), bezirkZirkBId = new BezirkZirkId("ServiceB");
    private static Location reception = new Location("OFFICE1", "BLOCK1", "RECEPTION");
    private static BezirkSadlManager bezirkSadlManager = null;
    private Set<BezirkDiscoveredZirk> discoveredZirkSet;

    private Set<BezirkZirkId> bezirkZirkIdSet;

    private BezirkZirkId dServiceId;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("############# Setting up DiscoveryTest TestCase ################");

        mockUtility.setUPTestEnv();
        bezirkSadlManager = mockUtility.bezirkSadlManager;

    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("############ Shutting down DiscoveryTest Testcase ############");
        mockUtility.destroyTestSetUp();

    }

    @Test
    public void testDiscoverServices() {

		/*Test1 : SadlManager should return null discoveredZirkSet when protocolRole is null.*/
        discoveredZirkSet = bezirkSadlManager.discoverZirks(null, reception);
        assertNull("DiscoveredZirk set is not null when services not available.", discoveredZirkSet);

		/*No services are subscribed to streamlessProtocol yet. 
		 * DiscoveredServiceSet should be null.
		 * */
        discoveredZirkSet = bezirkSadlManager.discoverZirks(subscribedStreamlessPRole, reception);
        assertNull("DiscoveredZirk set is not null when services not available.", discoveredZirkSet);
		
		
		/* ServiceA and ServiceB are registered and subscribed to StreamlessProtocolRole.
		 * ServiceB has its location set to null */
        bezirkSadlManager.registerService(bezirkZirkAId);
        bezirkSadlManager.subscribeService(bezirkZirkAId, subscribedStreamlessPRole);
        bezirkSadlManager.registerService(bezirkZirkBId);
        bezirkSadlManager.subscribeService(bezirkZirkBId, subscribedStreamlessPRole);
        bezirkSadlManager.setLocation(bezirkZirkBId, new Location(null, null, null));

        testDiscoveryWithSpecificLocation();
        testDiscoveryWithNullLocation();
    }


    /*Test2 : SadlManager is queried to discover services subscribed to StreamlessProtocolRole
     * 		  with specific location.
     */
    private void testDiscoveryWithSpecificLocation() {
		
		/*SadlManager is queried to discover services subscribed to StreamlessProtocolRole 
		 * near to reception.SadlManager should return null discoveredZirkSet */
        discoveredZirkSet = bezirkSadlManager.discoverZirks(subscribedStreamlessPRole,
                reception);
        assertNull("DiscoveredZirk set is not null when services not available.", discoveredZirkSet);
		
		/*ServiceA is set to the location "OFFICE1/BLOCK1/RECEPTION"*/
        bezirkSadlManager.setLocation(bezirkZirkAId, reception);
		
		/*	SadlManager should return serviceA for the discovery now. */
        bezirkZirkIdSet = discoverServicesUsingProtocolAndLocation(subscribedStreamlessPRole,
                reception);
        assertNotNull("No services found in discovery.", bezirkZirkIdSet);
        assertTrue("ServiceA was not discovered when queried for reception as location.", bezirkZirkIdSet.contains(bezirkZirkAId));
    }

    /*Test3 : SadlManager is queried to discover the services subscribed to StreamlessProtocolRole.
     * 		  As there is no location mentioned in the discovery request it should return
     *        both ServiceA and ServiceB.
     */
    private void testDiscoveryWithNullLocation() {

        bezirkZirkIdSet = discoverServicesUsingProtocolAndLocation(subscribedStreamlessPRole,
                null);
        assertNotNull("No services found in discovery.", bezirkZirkIdSet);
        assertTrue("ServiceA was not discovered when no location in request.", bezirkZirkIdSet.contains(bezirkZirkAId));
        assertTrue("ServiceB was not discovered when no location in request.", bezirkZirkIdSet.contains(bezirkZirkBId));
    }

    private Set<BezirkZirkId> discoverServicesUsingProtocolAndLocation(
            ProtocolRole protocolRole, Location loc) {
        discoveredZirkSet = bezirkSadlManager.discoverZirks(protocolRole, loc);
        bezirkZirkIdSet = getBezirkZirkIdSetOfDiscoveredZirks(discoveredZirkSet);
        return bezirkZirkIdSet;
    }

    private Set<BezirkZirkId> getBezirkZirkIdSetOfDiscoveredZirks(
            Set<BezirkDiscoveredZirk> discoveredServiceSet) {
        BezirkZirkEndPoint serviceEndPoint;
        bezirkZirkIdSet = null;

        if (discoveredServiceSet != null) {
            bezirkZirkIdSet = new HashSet<>();

            for (BezirkDiscoveredZirk discoveredService : discoveredServiceSet) {
                serviceEndPoint = (BezirkZirkEndPoint) (discoveredService
                        .getZirkEndPoint());
                dServiceId = serviceEndPoint.getBezirkZirkId();
                bezirkZirkIdSet.add(dServiceId);

            }
        }
        return bezirkZirkIdSet;
    }


}
