package com.bezirk.control.messages;

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

import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezrik.network.UhuNetworkUtilities;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the UnicastControlMessage by retrieving the field values after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class UnicastControlMessageTest {

	private static final Logger log = LoggerFactory
			.getLogger(UnicastControlMessageTest.class);

	private static final String sphereId = "TestSphere";
	private static final UhuServiceId serviceId = new UhuServiceId("ServiceA");
	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceId );
	private static final UhuServiceId serviceBId = new UhuServiceId("ServiceB");
	private static final UhuServiceEndPoint recipient = new UhuServiceEndPoint(serviceBId);
	private static final String key="TESTKEY";
	private static final Discriminator discriminator=Discriminator.DiscoveryRequest;
	private static final Boolean retransmit =true;
	
	private static InetAddress inetAddr;


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up UnicastControlMessageTest TestCase *****");
		inetAddr = getInetAddress();
		recipient.device = inetAddr.getHostAddress();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down UnicastControlMessageTest TestCase *****");
	}

	/**
	 * Test method for {@link com.bezirk.control.messages.MulticastControlMessage#MulticastControlMessage()}.
	 */
	@Test
	public void testUnicastControlMessage() {

	com.bezirk.control.messages.UnicastControlMessage unicastCtrlMessage = new com.bezirk.control.messages.UnicastControlMessage(sender, recipient, sphereId, discriminator, retransmit );
	String serializedMessage = unicastCtrlMessage.serialize();
	com.bezirk.control.messages.UnicastControlMessage deserializedCtrlMessage = com.bezirk.control.messages.UnicastControlMessage.deserialize(serializedMessage, com.bezirk.control.messages.UnicastControlMessage.class);
	assertEquals("Discriminator not equal to the set value.",discriminator, deserializedCtrlMessage.getDiscriminator());
	assertEquals("SphereId not equal to the set value.",sphereId, deserializedCtrlMessage.getSphereId());
	assertEquals("Retransmit not equal to the set value.",retransmit, deserializedCtrlMessage.getRetransmit());
	assertTrue("Local message considered as remote after deserialization", deserializedCtrlMessage.getIsLocal());
	/*--- TO BE UNCOMMENTED ONCE THE UHUSERVICEENDPOINT IS FIXED-----
	 * device null condition should be checked separately before device equals in UhuServiceEndpoint equals api.
	 * 
	 assertEquals("Sender not equal to the set value.",sender, deserializedCtrlMessage.getSender());
 	 assertEquals("Sender not equal to the set value.",recipient, deserializedCtrlMessage.getRecipient());
	 */
	unicastCtrlMessage =new com.bezirk.control.messages.UnicastControlMessage(sender, recipient, sphereId, discriminator, retransmit, key);
	serializedMessage = unicastCtrlMessage.serialize();
	deserializedCtrlMessage = com.bezirk.control.messages.UnicastControlMessage.deserialize(serializedMessage, UnicastControlMessage.class);
	assertEquals("Discriminator not equal to the set value.",discriminator, deserializedCtrlMessage.getDiscriminator());
	assertEquals("SphereId not equal to the set value.",sphereId, deserializedCtrlMessage.getSphereId());
	assertEquals("Retransmit not equal to the set value.",retransmit, deserializedCtrlMessage.getRetransmit());
	assertEquals("Key not equal to the set value.",key, deserializedCtrlMessage.getUniqueKey());
	assertTrue("Local message considered as remote after deserialization", deserializedCtrlMessage.getIsLocal());

	/*--- TO BE UNCOMMENTED ONCE THE UHUSERVICEENDPOINT IS FIXED-----
	 * device null condition should be checked separately before device equals in UhuServiceEndpoint equals api.
	 * 
	 assertEquals("Sender not equal to the set value.",sender, deserializedCtrlMessage.getSender());
	 assertEquals("Sender not equal to the set value.",recipient, deserializedCtrlMessage.getRecipient());
	 
	 */
	
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
