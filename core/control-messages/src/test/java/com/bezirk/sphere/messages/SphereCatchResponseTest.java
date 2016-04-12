package com.bezirk.sphere.messages;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.objects.UhuDeviceInfo;
import com.bezirk.api.objects.UhuDeviceInfo.UhuDeviceRole;
import com.bezirk.api.objects.UhuServiceInfo;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the SphereCatchResponse by retrieving the field values after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class SphereCatchResponseTest {
	
	private static final String SERVICE_A = "ServiceA";

	private static final Logger log = LoggerFactory
			.getLogger(SphereCatchResponseTest.class);

	private static final UhuServiceId serviceAId = new UhuServiceId(SERVICE_A);
	private static final UhuServiceInfo serviceAInfo = new UhuServiceInfo(serviceAId.getUhuServiceId(), SERVICE_A, "TEST", true, true); 
	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceAId);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up SphereCatchResponseTest TestCase *****");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down SphereCatchResponseTest TestCase *****");
	}

	
	@Test
	public void testSphereCatchResponse() {
		
		String catchSphereId ="MemberSphereID";
		String catchDeviceId="TESTDEVICEID2";
		List<UhuServiceInfo> serviceList = new ArrayList<UhuServiceInfo>();
		serviceList.add(serviceAInfo);
		UhuDeviceInfo services = new UhuDeviceInfo(catchDeviceId, "TESTDEVICE", "PC", UhuDeviceRole.UHU_MEMBER, true, serviceList );
		com.bezirk.sphere.messages.CatchResponse sphereCatchResonse = new com.bezirk.sphere.messages.CatchResponse(sender, catchSphereId, catchDeviceId, services );
		String serializedMessage = sphereCatchResonse.serialize();
		com.bezirk.sphere.messages.CatchResponse deserializedSphereCatchResponse = com.bezirk.sphere.messages.CatchResponse.deserialize(serializedMessage, com.bezirk.sphere.messages.CatchResponse.class);
		assertEquals("CatchedServices not equal to the set value.",services,deserializedSphereCatchResponse.getInviterSphereDeviceInfo());
		assertEquals("CatchSphereId not equal to the set value.",catchSphereId, deserializedSphereCatchResponse.getCatcherSphereId());
		assertEquals("CatchDeviceId not equal to the set value.",catchDeviceId, deserializedSphereCatchResponse.getCatcherDeviceId());
	}

}
