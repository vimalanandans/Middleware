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
//import com.bosch.upa.uhu.api.objects.UhuDeviceInfo;
//import com.bosch.upa.uhu.api.objects.UhuDeviceInfo.UhuDeviceRole;
//import com.bosch.upa.uhu.api.objects.UhuServiceInfo;
//import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
//import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
//
///**
// * This testCase verifies the SphereShareMemberResponse by retrieving the field values after deserialization.
// * 
// * @author AJC6KOR
// *
// */
//public class SphereShareMemberResponseTest {
//	
//	private static final Logger log = LoggerFactory
//			.getLogger(SphereShareMemberResponseTest.class);
//
//	private static final String sphereId = "TestSphere";
//	private static final String serviceAName = "ServiceA";
//	private static final UhuServiceId serviceAId = new UhuServiceId(serviceAName);
//	private static final UhuServiceInfo serviceAInfo = new UhuServiceInfo(serviceAId.getUhuServiceId(), serviceAName, "MEMBER",true, true);
//	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceAId );
//	private static List<UhuServiceInfo> services = new ArrayList<>();
//	
//	private static UhuDeviceInfo uhuDeviceInfo =null;
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		
//		log.info("***** Setting up SphereShareMemberResponseTest TestCase *****");
//		services.add(serviceAInfo);
//		uhuDeviceInfo= new UhuDeviceInfo("TESTDEVICEID", "TESTDEVICE", "PC", UhuDeviceRole.UHU_MEMBER, true, services);
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
//	String serializedMessage = sphereDiscoveryRequest.serialize();
//	SphereShareMemberResponse deserializedSphereDiscoveryResponse = SphereShareMemberResponse.deserialize(serializedMessage, SphereShareMemberResponse.class);
//	assertEquals("Deserialized SphereShareMemberResponse is having different device info.",uhuDeviceInfo, deserializedSphereDiscoveryResponse.getUhuDeviceInfo());
//	
//	}
//
//}
