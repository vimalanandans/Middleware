package com.bezirk.sadl;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test case is used to test sadlRegistry.
 * <p/>
 * SET UP : MockServiceA is subscribed to MockProtocolRole and to DummyProtocolRole.
 * MockserviceB is subscribed to MockProtocolRole
 * MockserviceC is subscribed to DummyProtocolRole.
 *
 * @author AJC6KOR
 */

/**
 * @author RHR8KOR
 *
 */
public class SadlRegistryTest {
    private final static Logger logger = LoggerFactory.getLogger(SadlRegistryTest.class);

    private static SadlRegistry sadlRegistry = null;

    private BezirkZirkId uhuServiceBId = new BezirkZirkId("ServiceB");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("############# Setting up SadlRegistry TestCase ################");
        sadlRegistry = new SadlRegistry();

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("****** Shutting down SadlRegistry Testcase *****");
    }

    @Test
    public void testSadlregistry() {

        logger.info("***** testing ClearRegistry *****");
        testClearRegistry();
        logger.info("***** testing equals() and hashcode() *****");
        checkEqualsHashcode();
    }

    /**
     * SadlRegistry is cleared. SadlRegistry is asked to verify whether ServiceB
     * is registered. It should return false.
     */
    private void testClearRegistry() {

        sadlRegistry.registerService(uhuServiceBId);

        sadlRegistry.clearRegistry();
        boolean isServiceRegistered = sadlRegistry.isServiceRegistered(uhuServiceBId);

        assertNotEquals(true, isServiceRegistered);

    }

    /**
     * The method checkEqualsHashcode() is to check the equals() and hascode()
     * overridden in SadlRegistry.java class
     */

    private void checkEqualsHashcode() {

		/*
         * ########################################### NOTE ##############################################
		 * For All maps used for e.g: sid ,protocolMap ,protocolDescMap ,eventMap ,streamMap,locationMap . 
		 * 								Refer SadlRegistry.java class.
		 * ###############################################################################################
		 */

		/* Creating the BezirkService Ids */

        BezirkZirkId bezirk = null;
        BezirkZirkId uhu1 = null;
        BezirkZirkId uhu2 = null;

        SadlRegistry sadleObj1 = null;
        SadlRegistry sadlobj2 = null;
        SadlRegistry sadlobj3 = null;

        Location loc = null;

        bezirk = new BezirkZirkId("ServiceA");
        uhu1 = new BezirkZirkId("ServiceB");
        uhu2 = new BezirkZirkId("ServiceB");

        loc = new Location("Kitchen", "Hall", "Room");

        sadleObj1 = creatingSadleObj1(bezirk);
        sadlobj2 = creatingsadlobj2(uhu1, loc);
        sadlobj3 = creatingsadlobj3(uhu1, uhu2, loc);
		

		/* Testing the EQUALITY of SADLEOBJ1 and sadlobj2 */


        boolean results = sadleObj1.equals(sadlobj2);

		/*
		 * Taking a variable "results" in which we are storing the result return
		 * by equals() method and hash code methods for checking the eqality
		 * between of sadleobj1, sadlobj2 and sadlobj3 by all possible cases.
		 */
		
			

		/*
		 * 1. Comparing the sadleobj1 with itself. Since, both are the same
		 * object result should be true if not then "Both Objects Are Not Equal"
		 * message will be thrown
		 */

        results = sadleObj1.equals(sadleObj1);
        assertTrue("Both Objects Are Not Equal", results);

		/*
		 * 2. Comparing sadleobj1 with sadlobj3. Since, sadleobj1 registered
		 * with bezirk and sadlobj3 registered with uhu2 the result will be false.
		 */

        results = sadleObj1.equals(sadlobj3);
        assertFalse("Both Objects having different Bezirk Zirk Ids", results);

		/*
		 * 3. Making sadlobj3 'null' and then comparing sadlobj2 with
		 * sadlobj2. Since, sadlobj3 is null it will not be equal to sadlobj2
		 * and result will be false.
		 */

        sadlobj3 = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("sadlobj3 is null", results);

        testeventandLcationEquality(uhu1, sadlobj2);
        testStreamMapEquality(uhu1, sadlobj2);
        testSidEquality(uhu2, sadlobj2, uhu1);
        testProtocolmap(uhu1, sadlobj2, uhu2);
        testProtocolDecMap(uhu1, uhu2, sadlobj2);
        testLocationMapEquality(uhu1, uhu2, sadlobj2);

        sadlobj3 = null;
        sadlobj2 = null;

        sadlobj2 = creatingSadleObj1(uhu1);
        sadlobj3 = creatingSadleObj1(uhu1);
        testEqualityAndHashCode(sadlobj2, sadlobj3);

    }

    private SadlRegistry creatingsadlobj3(BezirkZirkId uhu1,
                                          BezirkZirkId uhu2, Location loc) {
        SadlRegistry sadlobj3;
		/* Creating SADLEOBJECT3 */

        sadlobj3 = new SadlRegistry();
        sadlobj3.setLocation(uhu2, loc);
        sadlobj3.registerService(uhu2);
        sadlobj3.subscribeService(uhu1, new NewProtocolRole());
        return sadlobj3;
    }

    private SadlRegistry creatingsadlobj2(BezirkZirkId uhu1, Location loc) {
        SadlRegistry sadlobj2;
		/* Creating SADLEOBJECT2 */

        sadlobj2 = creatingsadlobj3(uhu1, uhu1, loc);
        return sadlobj2;
    }

    private SadlRegistry creatingSadleObj1(BezirkZirkId bezirk) {
        SadlRegistry sadleObj1;
		/* Creating the SADLEOBJECT1 */

        sadleObj1 = new SadlRegistry();
        sadleObj1.registerService(bezirk);
        sadleObj1.subscribeService(bezirk, new NewProtocolRole());
        return sadleObj1;
    }

    private void testEqualityAndHashCode(SadlRegistry sadlobj2,
                                         SadlRegistry sadlobj3) {
        boolean results;
        results = sadlobj2.equals(sadlobj3);
        assertTrue("sadlobj2 is not equal to sadlobj3", results);

		/*
		 * 17. Checking the hashcode equality for sadlobj2 and sadlobj3 only
		 * if both satisfied the equals() condition.
		 */

        results = sadlobj2.hashCode() == sadlobj3.hashCode();
        assertTrue("HashCode is not equal but the objects are equal", results);

        results = sadlobj2.equals("ServiceA");
        assertFalse("Both The Classes are not Comparable <Of Same Type>", results);

        sadlobj3.eventMap = null;
        sadlobj3.locationMap = null;
        sadlobj3.protocolMap = null;
        sadlobj3.streamMap = null;
        sadlobj3.protocolDescMap = null;
        sadlobj3.sid = null;

        results = sadlobj2.hashCode() == sadlobj3.hashCode();
        assertFalse("HashCode is equal but the objects are notequal", results);
    }

    private void testeventandLcationEquality(BezirkZirkId uhu1, SadlRegistry sadlobj2) {

        SadlRegistry sadlobj3;
        boolean results;
		/*
		 * 4. Making the eventMap for the sadlobj2 as null and then comparing
		 * with sadlobj2. Since, eventmap is null for sadlobj2 it will not be
		 * equal to sadlobj2 result will be false.
		 */

        sadlobj3 = creatingSadleObj1(uhu1);
        sadlobj3.eventMap = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("SADLEOBJEC3 eventmap is not null", results);

        results = sadlobj3.equals(sadlobj2);
        assertFalse("SADLEOBJEC2 eventmap is null", results);

        sadlobj3.eventMap = null;
        ConcurrentMap<String, Set<BezirkZirkId>> sadle2EventMap = sadlobj2.eventMap;
        sadlobj2.eventMap = null;
        results = sadlobj3.equals(sadlobj2);
        assertTrue("sadl registries with null eventmaps are not considered equal", results);

        sadlobj2.eventMap = sadle2EventMap;
		
		/*
		 * 5. Making eventMap and locationMap for sadlobj3 as empty and then
		 * comparing with sadlobj2. Since, maps are empty it should not be
		 * equal to sadlobj2 and the result will be false.
		 */
        sadlobj3.eventMap = new ConcurrentHashMap<String, Set<BezirkZirkId>>();
        sadlobj3.locationMap = new ConcurrentHashMap<BezirkZirkId, Location>();
        results = sadlobj2.equals(sadlobj3);
        assertFalse("SADLEOBJEC3 eventMap and location map is empty", results);

		/*
		 * 6. Making eventMap and locationMap as empty and protocolDescMap as
		 * null and then comparing with sadlobj2. 
		 * Result: False Guess Yourself Why ? :)
		 */

        sadlobj3.eventMap = new ConcurrentHashMap<String, Set<BezirkZirkId>>();
        sadlobj3.locationMap = new ConcurrentHashMap<BezirkZirkId, Location>();
        sadlobj3.protocolDescMap = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("SADLEOBJEC3 protocolDescMap is null and eventMap,locationMap isEmpty", results);

    }

    private void testStreamMapEquality(BezirkZirkId uhu1, SadlRegistry sadlobj2) {
        SadlRegistry sadlobj3;
        boolean results;
		/*
		 * 7. Registering sadlobj3 with uhu1 and making streamMap as null.
		 * 	  Result: false (Both Objects are not equal) 
		 *    Remark: streamMap for sadlobj2 is not null.
		 */

        sadlobj3 = creatingSadleObj1(uhu1);
        sadlobj3.streamMap = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("streamMap is null for sadlobj3", results);

		/* For sadlobj2 */

		/*
		 * 8. Making streamMap for sadlobj2 as null and comparing with sadlobj3. 
		 *     Result : false (sadlobj2 not equal to sadlobj3) 
		 *     Remark : streamMap is null for sadlobj2 but not for sadlobj3.
		 */

        sadlobj3 = creatingSadleObj1(uhu1);
        sadlobj2.streamMap = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("streamMap is null for sadlobj2", results);

		/*
		 * 9. Making streamMap for sadlobj2,sadlobj3 as null and comaparing with sadlobj3. 
		 *    Result : true (sadlobj2 equals to sadlobj3) 
		 *    Remark : streamMap is null for both sadlobj2,sadlobj3.
		 */

        sadlobj3 = creatingSadleObj1(uhu1);
        sadlobj3.streamMap = null;
        results = sadlobj2.equals(sadlobj3);
        assertTrue("streamMap is null for sadlobj2", results);
    }

    private void testSidEquality(BezirkZirkId uhu2, SadlRegistry sadlobj2, BezirkZirkId uhu1) {
        SadlRegistry sadlobj3;
        boolean results;
		/*
		 * 10. Making sid for sadlobj2 as null and then comparing with sadlobj3 
		 *     Result : false (sadlobj2 is not equal to sadlobj3) 
		 *     Remark : sid is not null for sadlobj2
		 */

        sadlobj3 = creatingSadleObj1(uhu2);
        sadlobj2.sid = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("SID is null for sadlobj2", results);

		/*
		 * 11. Registering sadlobj3 with uhu1 and then making sid of sadlobj3 as null. 
		 *     Result: false (Both Objects are not equal)
		 */

        sadlobj3 = creatingSadleObj1(uhu1);
        sadlobj3.sid = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("SID is null for sadlobj3", results);
		
		/* Sid not equal for sadlobj3 and sadlobj2*/
        sadlobj2.sid = new HashSet<>();
        sadlobj2.sid.add(new BezirkZirkId("ServiceA"));
        sadlobj3.sid = new HashSet<BezirkZirkId>();
        sadlobj3.sid.add(new BezirkZirkId("ServiceB"));
        results = sadlobj2.equals(sadlobj3);
        assertFalse("sadl registries with different sid maps are considered equal.", results);


    }

    private void testProtocolmap(BezirkZirkId uhu1, SadlRegistry sadlobj2,
                                 BezirkZirkId uhu2) {
        SadlRegistry sadlobj3;
        boolean results;
		/*
		 * 12. Making protocolMap for sadlobj2 as null and comparing with sadlobj3 
		 *     Result : false (sadlobj2 is not equal to sadlobj3) 
		 *     Remark : protocolMap for sadlobj2 is null.
		 */

        sadlobj3 = creatingSadleObj1(uhu1);
        sadlobj2.protocolMap = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("sadlobj2 protocolMap is null", results);

		/*
		 *  13. Registering sadlobj3 with bezirk zirkId 2 i.e. uhu2 and making protocolMap as null.
		 *      Result: False (Both objects are not equal)
		 */

        sadlobj3 = creatingSadleObj1(uhu2);
        sadlobj3.protocolMap = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("SADLEOBJEC3 protocolMap is null", results);
		
		/* ProtocolMap not equal for sadlobj3 and sadlobj2*/
        sadlobj2.protocolMap = new ConcurrentHashMap<>();
        sadlobj2.sid = new HashSet<>();
        sadlobj2.sid.add(new BezirkZirkId("ServiceA"));
        sadlobj2.protocolMap.put("Protocol2", sadlobj2.sid);
        sadlobj3.protocolMap = new ConcurrentHashMap<>();
        sadlobj3.sid = new HashSet<BezirkZirkId>();
        sadlobj3.sid.add(new BezirkZirkId("ServiceB"));
        sadlobj3.protocolMap.put("Protocol3", sadlobj3.sid);
        results = sadlobj2.equals(sadlobj3);
        assertFalse("sadl registries with different protocol maps are considered equal.", results);

    }

    private void testProtocolDecMap(BezirkZirkId uhu1, BezirkZirkId uhu2,
                                    SadlRegistry sadlobj2) {
        SadlRegistry sadlobj3;
        boolean results;
		/*
		 * 14. Making protocolDescMap for sadlobj2 as null and comparing with sadlobj3 
		 *     Result : false (sadlobj2 is not equal to sadlobj3) 
		 *     Remark : protocolDescMap for sadlobj2 is null.
		 */

        sadlobj3 = new SadlRegistry();
        sadlobj3.registerService(uhu2);
        sadlobj3.subscribeService(uhu1, new NewProtocolRole());
        sadlobj2.protocolDescMap = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("sadlobj2 protocolDescMap is null", results);
		
		
		
		/*
		 * 15. Making protocolDescMap for sadlobj3 as null and comparing with sadlobj2 
		 *     Result : false (sadlobj3 is not equal to sadlobj3) 
		 *     Remark : protocolDescMap for sadlobj3 is null.
		 */

        sadlobj3 = new SadlRegistry();
        sadlobj3.registerService(uhu2);
        sadlobj3.subscribeService(uhu1, new NewProtocolRole());
        sadlobj3.protocolDescMap = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("sadlobj3 protocolDescMap is null", results);
		
		/* ProtocolMap not equal for sadlobj3 and sadlobj2*/
        sadlobj2.protocolDescMap = new ConcurrentHashMap<>();
        sadlobj2.protocolDescMap.put("Protocol2", "Protocoldesc for protocol2");
        sadlobj3.protocolDescMap = new ConcurrentHashMap<>();
        sadlobj3.protocolDescMap.put("Protocol3", "Protocoldesc for protocol3");
        results = sadlobj2.equals(sadlobj3);
        assertFalse("sadl registries with different protocoldesc maps are considered equal.", results);

    }

    private void testLocationMapEquality(BezirkZirkId uhu1, BezirkZirkId uhu2,
                                         SadlRegistry sadlobj2) {
        SadlRegistry sadlobj3;
        boolean results;
		/*
		 * 16. Making locationMap for sadlobj2 as null and comparing with sadlobj3 
		 *     Result : false (sadlobj2 is not equal to sadlobj3) 
		 *     Remark : locationMap for sadlobj2 is null.
		 */

        sadlobj3 = new SadlRegistry();
        sadlobj3.registerService(uhu2);
        sadlobj3.subscribeService(uhu1, new NewProtocolRole());

        sadlobj2 = new SadlRegistry();
        sadlobj2.registerService(uhu2);
        sadlobj2.subscribeService(uhu1, new NewProtocolRole());

        sadlobj2.locationMap = null;
        results = sadlobj2.equals(sadlobj3);
        assertFalse("sadlobj2 locationMap is null", results);
		
		/* LocationMap not equal for sadlobj3 and sadlobj2*/
        sadlobj2.locationMap = new ConcurrentHashMap<>();
        sadlobj2.locationMap.put(new BezirkZirkId("Service2"), new Location("OFFICE1/FLOOR1/ROOM1"));
        sadlobj3.locationMap = new ConcurrentHashMap<>();
        sadlobj3.locationMap.put(new BezirkZirkId("Service3"), new Location("OFFICE3/FLOOR3/ROOM3"));
        results = sadlobj2.equals(sadlobj3);
        assertFalse("sadl registries with different location maps are considered equal.", results);
		
		/* LocationMap null for both sadlobje3 and sadlobje2*/

        sadlobj2.locationMap = null;
        sadlobj3.locationMap = null;
        results = sadlobj2.equals(sadlobj3);
        assertTrue("sadl registries with null location maps are not considered equal.", results);

    }

    class NewProtocolRole extends ProtocolRole {

        String events[] = {"NewProtocolRole"};
        String stream[] = {"NewProtocolRoleStream"};

        public String getProtocolName() {
            return "NewProtocolRole";
        }

        @Override
        public String getDescription() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String[] getEventTopics() {
            return events;
        }

        @Override
        public String[] getStreamTopics() {
            return stream;
        }

    }

    class NewProtocolRoleOther extends ProtocolRole {

        String events[] = {"NewProtocolRoleOther"};
        String stream[] = {"NewProtocolRoleOther"};

        String otherStream[] = {"NewProtocolRoleOtherStream"};

        public String getProtocolName() {
            return "NewProtocolRoleOther";
        }

        @Override
        public String getDescription() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String[] getEventTopics() {
            return events;
        }

        @Override
        public String[] getStreamTopics() {
            return stream;
        }

    }

}
