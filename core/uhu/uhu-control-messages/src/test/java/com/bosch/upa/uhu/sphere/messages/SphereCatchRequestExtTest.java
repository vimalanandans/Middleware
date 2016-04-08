package com.bosch.upa.uhu.sphere.messages;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.api.objects.UhuDeviceInfo;
import com.bosch.upa.uhu.api.objects.UhuDeviceInfo.UhuDeviceRole;
import com.bosch.upa.uhu.api.objects.UhuServiceInfo;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the SphereCatchRequestExt by retrieving the field values after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class SphereCatchRequestExtTest {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SphereCatchRequestExtTest.class);

	private static final UhuServiceId serviceAId = new UhuServiceId("ServiceA");
	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceAId);
	private static final UhuServiceId serviceBId = new UhuServiceId("ServiceB");

	private static final UhuServiceInfo serviceAInfo = new UhuServiceInfo(serviceAId.getUhuServiceId(), "ServiceA", "TESTA", true, true);
	private static final UhuServiceInfo serviceBInfo = new UhuServiceInfo(serviceBId.getUhuServiceId(), "ServiceB", "TESTB", true, true);
	private static List<UhuServiceInfo> services = new ArrayList<>();
	private static UhuDeviceInfo uhuDeviceInfo =null;



	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		LOGGER.info("***** Setting up SphereCatchRequestExtTest TestCase *****");
		services.add(serviceAInfo);
		services.add(serviceBInfo);
		uhuDeviceInfo = new UhuDeviceInfo("TESTDEVICE", "TEST", "PC", UhuDeviceRole.UHU_MEMBER, true, services );
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		LOGGER.info("***** Shutting down SphereCatchRequestExtTest TestCase *****");
	}

	
	@Test
	public void testSphereCatchRequestExt() {
		
		String qrServiceCatchSphereString="QRSTRING";
		String catchSphereId="CATCHSPHEREID";
		String scannedTempSphereId="TEMPID";
		CatchRequest sphereCatchRequestExt = new CatchRequest(sender, scannedTempSphereId, catchSphereId, uhuDeviceInfo, qrServiceCatchSphereString);
		String serializedMessage = sphereCatchRequestExt.serialize();
		CatchRequest deserializedSphereCatchRequestExt = CatchRequest.deserialize(serializedMessage, CatchRequest.class);
		assertEquals("SphereID not equal to the set value.",catchSphereId, deserializedSphereCatchRequestExt.getCatcherSphereId());
		assertEquals("UhuDeviceinfo not equal to the set value.",uhuDeviceInfo,deserializedSphereCatchRequestExt.getUhuDeviceInfo());
		assertEquals("CatchSphereString not equal to the set value.",qrServiceCatchSphereString, deserializedSphereCatchRequestExt.getSphereExchangeData());
	
	}


}
