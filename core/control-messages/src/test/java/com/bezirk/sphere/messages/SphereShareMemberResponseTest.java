//package com.bosch.upa.uhu.sphere.messages;
//
//import static org.junit.Assert.*;
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
//import BezirkDeviceInfo;
//import BezirkDeviceInfo.BezirkDeviceRole;
//import BezirkZirkInfo;
//import BezirkZirkEndPoint;
//import ZirkId;
//
///**
// * This testCase verifies the SphereShareMemberResponse by retrieving the field values after deserialization.
// * 
// * @author AJC6KOR
// *
// */
//public class SphereShareMemberResponseTest {
//	
//	private static final Logger logger = LoggerFactory
//			.getLogger(SphereShareMemberResponseTest.class);
//
//	private static final String sphereId = "TestSphere";
//	private static final String serviceAName = "ServiceA";
//	private static final ZirkId serviceAId = new ZirkId(serviceAName);
//	private static final BezirkZirkInfo serviceAInfo = new BezirkZirkInfo(serviceAId.getZirkId(), serviceAName, "MEMBER",true, true);
//	private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceAId );
//	private static List<BezirkZirkInfo> services = new ArrayList<>();
//	
//	private static BezirkDeviceInfo uhuDeviceInfo =null;
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		
//		log.info("***** Setting up SphereShareMemberResponseTest TestCase *****");
//		services.add(serviceAInfo);
//		uhuDeviceInfo= new BezirkDeviceInfo("TESTDEVICEID", "TESTDEVICE", "PC", BezirkDeviceRole.BEZIRK_MEMBER, true, services);
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//
//		log.info("***** Shutting down SphereShareMemberResponseTest TestCase *****");
//	}
//
//	@Test
//	public void testSphereShareMemberResponse() {
//
//	SphereShareMemberResponse sphereDiscoveryRequest = new SphereShareMemberResponse(sphereId, uhuDeviceInfo, sender);
//	String serializedMessage = sphereDiscoveryRequest.toJson();
//	SphereShareMemberResponse deserializedSphereDiscoveryResponse = SphereShareMemberResponse.fromJson(serializedMessage, SphereShareMemberResponse.class);
//	assertEquals("Deserialized SphereShareMemberResponse is having different device info.",uhuDeviceInfo, deserializedSphereDiscoveryResponse.getBezirkDeviceInfo());
//	
//	}
//
//}
