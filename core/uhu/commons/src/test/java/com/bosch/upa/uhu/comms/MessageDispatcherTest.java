package com.bosch.upa.uhu.comms;

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

import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.discovery.DiscoveryRequest;
import com.bosch.upa.uhu.control.messages.discovery.DiscoveryResponse;
import com.bosch.upa.uhu.control.messages.streaming.StreamRequest;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sadl.ISadlEventReceiver;
import com.bosch.upa.uhu.sadl.UhuSadlManager;

/**
 * This testcase tests the working of message dispatcher. A mock receiver is registered for different control messages with the message dispatcher.
 * Tests verifies that the control messages are properly routed to the receiver based on the discriminators registered wth message dispatcher.
 * 
 * @author ajc6kor
 *
 */
public class MessageDispatcherTest {

	private static final Logger log = LoggerFactory.getLogger(MessageDispatcherTest.class);
	private static final UhuServiceId serviceId = new UhuServiceId("ServiceA");
	private static final UhuServiceEndPoint recipient = new UhuServiceEndPoint(serviceId );
	private static InetAddress inetAddr;

	boolean requestReceived =false;
	boolean responseReceived = false;
	boolean unKnownMessageReceived = false;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up MessageDispatcherTest TestCase *****");
		inetAddr = getInetAddress();
		recipient.device = inetAddr.getHostAddress();
	}
	
	@Test
	public void test() {

		ISadlEventReceiver uhusadlManager = new UhuSadlManager(null);
		MessageDispatcher messageDispatcher = new MessageDispatcher(uhusadlManager );
		
		ICtrlMsgReceiver receiver = new MockReceiver();
		messageDispatcher.registerControlMessageReceiver(ControlMessage.Discriminator.DiscoveryRequest, receiver);
		
		ControlLedger tcMessage = new ControlLedger();
		UhuServiceEndPoint sender = new UhuServiceEndPoint("DeviceA", new UhuServiceId("MockServiceA"));
		ControlMessage discoveryRequest = new DiscoveryRequest(null, sender , null, null, 0, 0, 0);
		tcMessage.setMessage(discoveryRequest );
		messageDispatcher.dispatchControlMessages(tcMessage );
		
		assertTrue("Request is not recieved by mock receiver.",requestReceived);
		
		messageDispatcher.registerControlMessageReceiver(ControlMessage.Discriminator.DiscoveryResponse, receiver);

		ControlMessage discoveryResponse = new DiscoveryResponse(recipient, null, null, 0);
		tcMessage.setMessage(discoveryResponse );
		messageDispatcher.dispatchControlMessages(tcMessage );
		
		assertTrue("Response is not recieved by mock receiver.",responseReceived);

		ControlMessage streamRequest = new StreamRequest(null, recipient, null, null, null, null, null, null, true, true, true, (short) 0);
		tcMessage.setMessage(streamRequest );
		messageDispatcher.dispatchControlMessages(tcMessage );
		
		assertFalse("Unknown Message type is recieved by mock receiver.",unKnownMessageReceived);
		
		ICtrlMsgReceiver duplicateReceiver = new MockReceiver(); 
		assertFalse("Duplicte receiver is allowed to register for the same message type.",messageDispatcher.registerControlMessageReceiver(ControlMessage.Discriminator.DiscoveryRequest, duplicateReceiver));


	
	}
	
	
	   class MockReceiver implements ICtrlMsgReceiver{
		   
	        @Override
	        public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
	            switch (id)
	            {
	                case DiscoveryRequest:
	                	log.info("Received discovery request.");
	                    requestReceived=true;
	                    break;
	                case DiscoveryResponse:
	                	log.info("Received discovery response.");
	                	responseReceived = true;
	                	break;
	                 default:
	                    log.error("Unknown control message > "+id);
	                    unKnownMessageReceived=true;
	                    return false;
	            }
	            return true;
	        }
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
