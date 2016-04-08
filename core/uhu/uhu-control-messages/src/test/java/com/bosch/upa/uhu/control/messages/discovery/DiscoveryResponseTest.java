package com.bosch.upa.uhu.control.messages.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.control.messages.ControlMessage.Discriminator;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the DiscoveryResponse by retrieving the field values after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class DiscoveryResponseTest {
	
	private static final Logger log = LoggerFactory
			.getLogger(DiscoveryResponseTest.class);

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
		
		log.info("***** Setting up DiscoveryResponseTest TestCase *****");
		inetAddr = getInetAddress();
		recipient.device = inetAddr.getHostAddress();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down DiscoveryResponseTest TestCase *****");
	}



	
	@Test
	public void testDiscoveryResponse() {
		
		DiscoveryResponse discoveryResponse = new DiscoveryResponse(recipient, sphereId, requestKey, discoveryId);
		String serializedMessage = discoveryResponse.serialize();
		DiscoveryResponse deserializedDiscoveryResponse = DiscoveryResponse.deserialize(serializedMessage, DiscoveryResponse.class);
		assertEquals("DiscoveryId not equal to the set value.",Integer.valueOf(discoveryId), deserializedDiscoveryResponse.getReqDiscoveryId());
		assertEquals("Discriminator not set properly.",Discriminator.DiscoveryResponse, deserializedDiscoveryResponse.getDiscriminator());
		assertNotNull("Discovered Service list is null", deserializedDiscoveryResponse.getServiceList());

		
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