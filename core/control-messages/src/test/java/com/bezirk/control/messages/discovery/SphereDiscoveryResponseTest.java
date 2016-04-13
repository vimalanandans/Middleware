package com.bezirk.control.messages.discovery;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.objects.UhuSphereInfo;
import com.bezrik.network.UhuNetworkUtilities;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the DiscoveryResponse by retrieving the field values after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class SphereDiscoveryResponseTest {
	
	private static final Logger log = LoggerFactory
			.getLogger(SphereDiscoveryResponseTest.class);

	private static final String sphereId = "TestSphere";
	private static final UhuServiceId serviceId = new UhuServiceId("ServiceA");
	private static final UhuServiceEndPoint recipient = new UhuServiceEndPoint(serviceId );
	private static final String requestKey = "REQUEST_KEY";
	private int discoveryId =2;
	private static InetAddress inetAddr;

	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up SphereDiscoveryResponseTest TestCase *****");
		inetAddr = getInetAddress();
		recipient.device = inetAddr.getHostAddress();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down SphereDiscoveryResponseTest TestCase *****");
	}



	
	@Test
	public void testSphereDiscoveryResponse() {
		
		com.bezirk.control.messages.discovery.SphereDiscoveryResponse sphereDiscoveryResponse = new com.bezirk.control.messages.discovery.SphereDiscoveryResponse(recipient, sphereId, requestKey, discoveryId);
		UhuSphereInfo uhuSphereInfo = new UhuSphereInfo("TestSphereID", "Test", null, null, null);
		sphereDiscoveryResponse.setUhuSphereInfo(uhuSphereInfo );
		String serializedMessage = sphereDiscoveryResponse.serialize();
		com.bezirk.control.messages.discovery.SphereDiscoveryResponse deserializedSphereDiscoveryResponse = com.bezirk.control.messages.discovery.SphereDiscoveryResponse.deserialize(serializedMessage, com.bezirk.control.messages.discovery.SphereDiscoveryResponse.class);
		assertEquals("UhuSphereInfo not equal to the set value.",uhuSphereInfo.getSphereID(), deserializedSphereDiscoveryResponse.getUhuSphereInfo().getSphereID());
		assertEquals("DiscoveryId not equal to the set value.",Integer.valueOf(discoveryId), deserializedSphereDiscoveryResponse.getReqDiscoveryId());


		
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

			log.error("Unable to fetch network interface");

		}
		return null;
	}

}