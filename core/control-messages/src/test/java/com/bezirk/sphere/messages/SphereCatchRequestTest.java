//package com.bosch.upa.uhu.sphere.messages;
//
//import static org.junit.Assert.assertEquals;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import SphereVitals;
//import UhuServiceEndPoint;
//import UhuServiceId;
//
///**
// * This testCase verifies the SphereCatchRequest by retrieving the field values after deserialization.
// * 
// * @author AJC6KOR
// *
// */
//public class SphereCatchRequestTest {
//	
//	private static final Logger log = LoggerFactory
//			.getLogger(SphereCatchRequestTest.class);
//
//	private static final UhuServiceId serviceAId = new UhuServiceId("ServiceA");
//	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceAId);
//	private static final UhuServiceId serviceBId = new UhuServiceId("ServiceB");
//
//	private static List<UhuServiceId> services = new ArrayList<>();
//
//
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		
//		log.info("***** Setting up SphereCatchRequestTest TestCase *****");
//		services.add(serviceAId);
//		services.add(serviceBId);
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//
//		log.info("***** Shutting down SphereCatchRequestTest TestCase *****");
//	}
//
//	
//	@Test
//	public void testSphereCatchRequest() {
//		
//		String joinSphereId ="MemberSphereID";
//		String joinSphereType="Member";
//		String scannedSphereId="HomeSphereID";
//		String joinDeviceId="TESTDEVICEID2";
//		String joinDeviceName="TESTDEVICE2";
//		String joinDeviceType="PC";
//		SphereVitals sphereVitals = new SphereVitals("Testkey".getBytes(), "PUBLIC".getBytes());
//		String joinSphereName ="Home"; 
//		SphereCatchRequest sphereCatchRequest = new SphereCatchRequest(joinSphereId, joinSphereType, scannedSphereId,
//				services, sender, joinDeviceId, joinDeviceName, joinDeviceType, sphereVitals, joinSphereName);
//		String serializedMessage = sphereCatchRequest.serialize();
//		SphereCatchRequest deserializedSphereCatchRequest = SphereCatchRequest.deserialize(serializedMessage, SphereCatchRequest.class);
//		assertEquals("Services not equal to the set value.",services,deserializedSphereCatchRequest.getServices());
//		assertEquals("JoinSphereId not equal to the set value.",joinSphereId, deserializedSphereCatchRequest.getJoinSphereId());
//		assertEquals("JoinDeviceId not equal to the set value.",joinDeviceId, deserializedSphereCatchRequest.getJoinDeviceId());
//		assertEquals("JoinDeviceName not equal to the set value.",joinDeviceName, deserializedSphereCatchRequest.getJoinDeviceName());
//		assertEquals("JoinDeviceType not equal to the set value.",joinDeviceType, deserializedSphereCatchRequest.getJoinDeviceType());
//		assertEquals("JoinSphereName not equal to the set value.",joinSphereName, deserializedSphereCatchRequest.getJoinSphereName());
//		assertEquals("JoinSphereType not equal to the set value.",joinSphereType, deserializedSphereCatchRequest.getJoinSphereType());
//		assertEquals("SphereVitals not equal to the set value.",sphereVitals, sphereCatchRequest.getSphereVitals());
//	}
//
//}
