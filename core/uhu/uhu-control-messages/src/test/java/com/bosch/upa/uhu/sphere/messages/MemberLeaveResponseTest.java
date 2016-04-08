package com.bosch.upa.uhu.sphere.messages;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the MemberLeaveResponse by retrieving the field values after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class MemberLeaveResponseTest {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MemberLeaveResponseTest.class);

	private static final String sphereId = "TestSphere";
	private static final String sphereName = "Test";
	private static final UhuServiceId serviceId = new UhuServiceId("ServiceA");
	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceId );
	private static final UhuServiceId serviceBId = new UhuServiceId("ServiceB");
	private static final UhuServiceEndPoint recipient = new UhuServiceEndPoint(serviceBId);
	private static final String key="TESTKEY";

	private static InetAddress inetAddr;


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		LOGGER.info("***** Setting up MemberLeaveResponseTest TestCase *****");
		inetAddr = getInetAddress();
		recipient.device = inetAddr.getHostAddress();

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		LOGGER.info("***** Shutting down MemberLeaveResponseTest TestCase *****");
	}

	
	@Test
	public void testMemberLeaveResponse() {
		
		MemberLeaveResponse memberLeaveResponse = new MemberLeaveResponse(sphereId, 0, true, true, sender, recipient, serviceId, sphereName, key);
		String serializedMessage = memberLeaveResponse.serialize();
		MemberLeaveResponse deserializedMemberLeaveResponse = MemberLeaveResponse.deserialize(serializedMessage, MemberLeaveResponse.class);
		assertEquals("ServiceId not equal to the set value.",serviceId, deserializedMemberLeaveResponse.getServiceId());
		assertEquals("SphereName not equal to the set value.",sphereName,deserializedMemberLeaveResponse.getSphere_Name());
		assertTrue("IsRemovedSuccessfully not equal to the set value.", deserializedMemberLeaveResponse.isRemovedSuccessfully());
		assertTrue("IsSignatureVerified not equal to the set value.", deserializedMemberLeaveResponse.isSignatureVerified());
	
	}

	private static InetAddress getInetAddress() {
		try {

			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {

					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()
							&& inetAddress.isSiteLocalAddress()) {

						inetAddr = UhuNetworkUtilities.getIpForInterface(intf);
						return inetAddr;
					}

				}
			}
		} catch (SocketException e) {

			LOGGER.error("Unable to fetch network interface");

		}
		return null;
	}

}
