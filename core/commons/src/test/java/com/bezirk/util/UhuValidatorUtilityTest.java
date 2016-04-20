package com.bezirk.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezrik.network.UhuNetworkUtilities;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * @author ajc6kor
 *
 */
public class UhuValidatorUtilityTest {

	private static final Logger log = LoggerFactory.getLogger(UhuValidatorUtilityTest.class);

	static UhuServiceEndPoint sender = new UhuServiceEndPoint(new UhuServiceId("MockServiceA"));
	static String serviceId = "MockServiceB";
	static UhuServiceId uhuServiceId = new UhuServiceId(serviceId);
	static UhuServiceEndPoint recipient= new UhuServiceEndPoint(uhuServiceId);
	String sphereId = "testSphere";
	private static InetAddress inetAddr;
	boolean isValid= false;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		inetAddr = getInetAddress();
		recipient.device = inetAddr.getHostAddress();
		sender.device = inetAddr.getHostAddress();
	}
	
	
	@Test
	public void test() {
		
		testCheckDiscoveryRequest();

		testCheckHeader();

		testCheckLoggingServiceMessage();

		testCheckProtocolRole();

		testCheckRTCStreamRequest();
		
		testCheckStreamRequest();
		
		testCheckUhuServiceId();
		
		testChekUhuServiceEndPoint();

		

	}


	private void testChekUhuServiceEndPoint() {
		
		/*-------------- Positive cases --------------*/
		isValid = UhuValidatorUtility.checkUhuServiceEndPoint(recipient);
		assertTrue("Non null serviceEndpoint is considered invalid by validator",isValid);
		
		/*-------------- Negative cases --------------*/
		UhuServiceId uhuSid=null;
		UhuServiceEndPoint uhuServiceEndPoint = new UhuServiceEndPoint(uhuSid);
		isValid = UhuValidatorUtility.checkUhuServiceEndPoint(uhuServiceEndPoint );
		assertFalse("Null serviceEndpoint is considered valid by validator",isValid);
		
		uhuSid = new UhuServiceId(null); 
		uhuServiceEndPoint = new UhuServiceEndPoint(uhuSid);
		isValid = UhuValidatorUtility.checkUhuServiceEndPoint(uhuServiceEndPoint );
		assertFalse("Null serviceEndpoint is considered valid by validator",isValid);
		
		uhuServiceEndPoint = new UhuServiceEndPoint(uhuServiceId);
		isValid = UhuValidatorUtility.checkUhuServiceEndPoint(uhuServiceEndPoint );
		assertFalse("Null serviceEndpoint is considered valid by validator",isValid);
		
	}


	private void testCheckUhuServiceId() {
		
		/*-------------- Positive cases --------------*/
		isValid = UhuValidatorUtility.checkUhuServiceId(uhuServiceId);
		assertTrue("Non null serviceID is considered invalid by validator",isValid);
		
		/*-------------- Negative cases --------------*/
		isValid = UhuValidatorUtility.checkUhuServiceId(null);
		assertFalse("Null serviceID is considered valid by validator",isValid);
		
		isValid = UhuValidatorUtility.checkUhuServiceId(new UhuServiceId(null));
		assertFalse("Null serviceID is considered valid by validator",isValid);
	}


	private void testCheckStreamRequest() {
		
		/*-------------- Positive cases --------------*/
		StreamRequest request = new StreamRequest(sender, recipient, sphereId, 
				null, null, "testString", "testLabel", "testFile", true, true, true, (short)3);
		isValid = UhuValidatorUtility.checkStreamRequest(request);
		assertTrue("Valid streamRequest is considered invalid by validator.",isValid);
		
		/*-------------- Negative cases --------------*/
		isValid = UhuValidatorUtility.checkStreamRequest(null);
		assertFalse("Invalid streamRequest is considered valid by validator.",isValid);
		
		request = new StreamRequest(sender, recipient, sphereId, 
				null, null, null, "testLabel", "testFile", true, true, true, (short)3);
		isValid = UhuValidatorUtility.checkStreamRequest(request);
		assertFalse("Invalid streamRequest is considered valid by validator.",isValid);
		
		request = new StreamRequest(sender, recipient, sphereId, 
				null, null, "testString", "testLabel", null, true, true, true, (short)3);
		isValid = UhuValidatorUtility.checkStreamRequest(request);
		assertFalse("Invalid streamRequest is considered valid by validator.",isValid);

		request = new StreamRequest(sender, recipient, sphereId, 
				null, null, "testString", null, "testFile", true, true, true, (short)3);
		isValid = UhuValidatorUtility.checkStreamRequest(request);
		assertFalse("Invalid streamRequest is considered valid by validator.",isValid);
		
		request = new StreamRequest(sender, recipient, null, 
				null, null, "testString", "testLabel", "testFile", true, true, true, (short)3);
		isValid = UhuValidatorUtility.checkStreamRequest(request);
		assertFalse("Invalid streamRequest is considered valid by validator.",isValid);
		
		request = new StreamRequest(null, recipient, sphereId, 
				null, null, "testString", "testLabel", "testFile", true, true, true, (short)3);
		isValid = UhuValidatorUtility.checkStreamRequest(request);
		assertFalse("Invalid streamRequest is considered valid by validator.",isValid);
		
		UhuServiceEndPoint recepient = new UhuServiceEndPoint(new UhuServiceId("test"));
		recepient.device="";
		request = new StreamRequest(sender, recepient, sphereId, 
				null, null, "testString", "testLabel", "testFile", true, true, true, (short)3);
		isValid = UhuValidatorUtility.checkStreamRequest(request);
		assertFalse("Invalid streamRequest is considered valid by validator.",isValid);
		
	}


	private void testCheckRTCStreamRequest() {
		
		/*-------------- Positive cases --------------*/
		isValid = UhuValidatorUtility.checkRTCStreamRequest(uhuServiceId, recipient);
		assertTrue("Valid RTCStreamRequest is considered invalid by validator.",isValid);
		
		/*-------------- Negative cases --------------*/
		isValid = UhuValidatorUtility.checkRTCStreamRequest(null, recipient);
		assertFalse("Invalid RTCStreamRequest is considered valid by validator.",isValid);
		
		isValid = UhuValidatorUtility.checkRTCStreamRequest(uhuServiceId, null);
		assertFalse("Invalid RTCStreamRequest is considered valid by validator.",isValid);
		
	}


	private void testCheckProtocolRole() {
		
		/*-------------- Positive cases --------------*/
		MockProtocolRole pRole = new MockProtocolRole();
		SubscribedRole role = new SubscribedRole(pRole);
		isValid = UhuValidatorUtility.checkProtocolRole(role);
		assertTrue("Valid protocolrole is considered invalie by validator.",isValid);
		
		/*-------------- Negative cases --------------*/
		isValid = UhuValidatorUtility.checkProtocolRole(null);
		assertFalse("Null protocolrole is considered valid by validator.",isValid);
		
		pRole.setProtocolName(null);
		role = new SubscribedRole(pRole);
		isValid = UhuValidatorUtility.checkProtocolRole(role);
		assertFalse("Null protocolrole is considered valid by validator.",isValid);
		
	}


	private void testCheckLoggingServiceMessage() {
		
		/*-------------- Positive cases --------------*/
		String serverIp="123.12.10.2";
		String[] sphereList =new String[]{"Sphere1","Sphere2"};
		LoggingServiceMessage logServiceMsg = new LoggingServiceMessage(sender, sphereId, serverIp, 2020, sphereList , true);
		isValid = UhuValidatorUtility.checkLoggingServiceMessage(logServiceMsg );
		assertTrue("Valid logServiceMessage is considered invalid by validator.",isValid);
		
		/*-------------- Negative cases --------------*/
		isValid = UhuValidatorUtility.checkLoggingServiceMessage(null);
		assertFalse("Invalid logServiceMessage is considered valid by validator.",isValid);
		
		logServiceMsg = new LoggingServiceMessage(sender, sphereId, null, 2020, sphereList , true);
		isValid = UhuValidatorUtility.checkLoggingServiceMessage(logServiceMsg);
		assertFalse("Invalid logServiceMessage is considered valid by validator.",isValid);
		
		logServiceMsg = new LoggingServiceMessage(sender, sphereId, serverIp, 2020, null , true);
		isValid = UhuValidatorUtility.checkLoggingServiceMessage(logServiceMsg);
		assertFalse("Invalid logServiceMessage is considered valid by validator.",isValid);
		
		sphereList = new String[]{};
		logServiceMsg = new LoggingServiceMessage(sender, sphereId, serverIp, 2020, sphereList , true);
		isValid = UhuValidatorUtility.checkLoggingServiceMessage(logServiceMsg);
		assertFalse("Invalid logServiceMessage is considered valid by validator.",isValid);

		logServiceMsg = new LoggingServiceMessage(sender, sphereId, serverIp, -1, sphereList , true);
		isValid = UhuValidatorUtility.checkLoggingServiceMessage(logServiceMsg);
		assertFalse("Invalid logServiceMessage is considered valid by validator.",isValid);
	}


	private void testCheckHeader() {
		
		/*-------------- Positive cases --------------*/
		Header mHeader = new Header(sphereId, sender, "12", "test");
		isValid =UhuValidatorUtility.checkHeader(mHeader );
		assertTrue("Valid header is considered invalid by validator.",isValid);
		
		/*-------------- Negative cases --------------*/
		mHeader = new Header(null, sender, "12", "test");
		isValid =UhuValidatorUtility.checkHeader(mHeader );
		assertFalse("Invalid header is considered valid by validator.",isValid);
		
		mHeader = new Header(sphereId, sender, "12", null);
		isValid =UhuValidatorUtility.checkHeader(mHeader );
		assertFalse("Invalid header is considered valid by validator.",isValid);
		
		mHeader = new Header(sphereId, null, "12", "test");
		isValid =UhuValidatorUtility.checkHeader(mHeader );
		assertFalse("Invalid header is considered valid by validator.",isValid);
		
		
		
	}


	private void testCheckDiscoveryRequest() {

		/*-------------- Positive cases --------------*/
		DiscoveryRequest discoveryRequest  = new DiscoveryRequest(sphereId, sender, null, null, 2, 90000, 3);
		isValid =UhuValidatorUtility.checkDiscoveryRequest(discoveryRequest);
		assertTrue("Valid discoveryRequest is considered invalid by validator.",isValid);
		
		
		/*-------------- Negative cases --------------*/
		discoveryRequest = new DiscoveryRequest(null, sender, null, null, 2, 60000, 3);
		isValid = UhuValidatorUtility.checkDiscoveryRequest(discoveryRequest);
		assertFalse("Invalid discoveryRequest is considered valid by validator.",isValid);
		
		UhuServiceEndPoint sep= new UhuServiceEndPoint(uhuServiceId);
		discoveryRequest = new DiscoveryRequest(sphereId, sep, null, null, 2, 60000, 3);
		isValid = UhuValidatorUtility.checkDiscoveryRequest(discoveryRequest);
		assertFalse("Invalid discoveryRequest is considered valid by validator.",isValid);
		
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

	 class MockProtocolRole extends ProtocolRole{

		 String protocolName = this.getClass().getSimpleName();
	
		public void setProtocolName(String protocolName) {
			this.protocolName = protocolName;
		}

		@Override
		public String getProtocolName() {
			return protocolName;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String[] getEventTopics() {
			return null;
		}

		@Override
		public String[] getStreamTopics() {
			return null;
		}
		 
		 
	 }
}
