package com.bezirk.control.messages.streaming;

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

import com.bezirk.api.addressing.Location;
import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezrik.network.UhuNetworkUtilities;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the StreamRequest POJO by retrieving the field discriminator.
 * 
 * @author AJC6KOR
 *
 */
public class StreamRequestTest {
	
	private static final Logger log = LoggerFactory
			.getLogger(StreamRequestTest.class);

	private static final String sphereName="Home";
	private static final Location location = new Location("OFFICE1", "BLOCk1", "ROOM1");
	private static final String key= "TESTKEY";
	private static final UhuServiceId serviceAId = new UhuServiceId("ServiceA");
	private static final UhuServiceId serviceBId = new UhuServiceId("ServiceB") ;
	private static final UhuServiceEndPoint recipient = new UhuServiceEndPoint(serviceAId);
	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceBId);
	private static final short localStreamId =10;
	private static InetAddress inetAddr;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up StreamRequestTest TestCase *****");
		inetAddr = getInetAddress();
		recipient.device = inetAddr.getHostAddress();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down StreamRequestTest TestCase *****");
	}

	@Test
	public void testStreamRequest() {
		
		com.bezirk.control.messages.streaming.StreamRequest streamRequest =new com.bezirk.control.messages.streaming.StreamRequest(sender, recipient, sphereName, key, location, "TESTSTREAM", "TEST",null, true, true, false, localStreamId);
		assertEquals("StreamRequest is not having discriminator set properly.",Discriminator.StreamRequest, streamRequest.getDiscriminator());

		
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
