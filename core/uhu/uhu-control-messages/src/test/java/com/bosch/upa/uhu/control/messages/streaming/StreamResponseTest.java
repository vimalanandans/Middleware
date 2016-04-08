package com.bosch.upa.uhu.control.messages.streaming;

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

import com.bosch.upa.uhu.control.messages.ControlMessage.Discriminator;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.streaming.control.Objects.StreamRecord.StreamingStatus;

/**
 * This testCase verifies the StreamResponse POJO by retrieving the field discriminator.
 * 
 * @author AJC6KOR
 *
 */
public class StreamResponseTest {
	
	private static final Logger log = LoggerFactory
			.getLogger(StreamResponseTest.class);

	private static final String sphereName="Home";
	private static final UhuServiceId serviceAId = new UhuServiceId("ServiceA");
	private static final UhuServiceId serviceBId = new UhuServiceId("ServiceB") ;
	private static final UhuServiceEndPoint recipient = new UhuServiceEndPoint(serviceAId);
	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceBId);
	private String strmKey="STREAM_KEY";

	private String streamIp = UhuNetworkUtilities.getDeviceIp();
	private int streamPort=7999;
	private static InetAddress inetAddr;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up StreamResponseTest TestCase *****");
		inetAddr = getInetAddress();
		recipient.device = inetAddr.getHostAddress();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down StreamResponseTest TestCase *****");
	}

	@Test
	public void testStreamResponse() {
		
		StreamResponse streamResponse =new StreamResponse(sender, recipient, sphereName, strmKey, StreamingStatus.READY, streamIp,streamPort);
		assertEquals("StreamResponse is not having discriminator set properly.",Discriminator.StreamResponse, streamResponse.getDiscriminator());

		
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
