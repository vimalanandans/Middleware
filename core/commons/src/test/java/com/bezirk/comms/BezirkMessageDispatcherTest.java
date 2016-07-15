package com.bezirk.comms;

import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.discovery.DiscoveryResponse;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.pubsubbroker.PubSubEventReceiver;
import com.bezrik.network.BezirkNetworkUtilities;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This testcase tests the working of message dispatcher. A mock receiver is registered for different control messages with the message dispatcher.
 * Tests verifies that the control messages are properly routed to the receiver based on the discriminators registered wth message dispatcher.
 *
 * @author ajc6kor
 */
public class BezirkMessageDispatcherTest {

    private static final Logger logger = LoggerFactory.getLogger(BezirkMessageDispatcherTest.class);
    private static final ZirkId serviceId = new ZirkId("ServiceA");
    private static final BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(serviceId);
    private static InetAddress inetAddr;

    boolean requestReceived = false;
    boolean responseReceived = false;
    boolean unKnownMessageReceived = false;

    @BeforeClass
    public static void setUpBeforeClass() {

        logger.info("***** Setting up BezirkMessageDispatcherTest TestCase *****");
        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();
    }

    private static InetAddress getInetAddress() {
        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {

                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()
                            && inetAddress.isSiteLocalAddress()) {

                        inetAddr = BezirkNetworkUtilities.getIpForInterface(intf);
                        return inetAddr;
                    }

                }
            }
        } catch (SocketException e) {
            logger.error("Unable to fetch network interface");
        }
        return null;
    }

    @Test
    public void test() {
        PubSubEventReceiver bezirkSadlManager = new PubSubBroker(null);
        CommsMessageDispatcher commsMessageDispatcher = new CommsMessageDispatcher(bezirkSadlManager);

        CtrlMsgReceiver receiver = new MockReceiver();
        commsMessageDispatcher.registerControlMessageReceiver(ControlMessage.Discriminator.DiscoveryRequest, receiver);

        ControlLedger tcMessage = new ControlLedger();
        BezirkZirkEndPoint sender = new BezirkZirkEndPoint("DeviceA", new ZirkId("MockServiceA"));
        ControlMessage discoveryRequest = new DiscoveryRequest(null, sender, null, null, 0, 0, 0);
        tcMessage.setMessage(discoveryRequest);
        commsMessageDispatcher.dispatchControlMessages(tcMessage);

        assertTrue("Request is not recieved by mock receiver.", requestReceived);

        commsMessageDispatcher.registerControlMessageReceiver(ControlMessage.Discriminator.DiscoveryResponse, receiver);

        ControlMessage discoveryResponse = new DiscoveryResponse(recipient, null, null, 0);
        tcMessage.setMessage(discoveryResponse);
        commsMessageDispatcher.dispatchControlMessages(tcMessage);

        assertTrue("Response is not recieved by mock receiver.", responseReceived);

        ControlMessage streamRequest = new StreamRequest(null, recipient, null, null, null, null, null, null, true, true, true, (short) 0);
        tcMessage.setMessage(streamRequest);
        commsMessageDispatcher.dispatchControlMessages(tcMessage);

        assertFalse("Unknown Message type is recieved by mock receiver.", unKnownMessageReceived);

        CtrlMsgReceiver duplicateReceiver = new MockReceiver();
        assertFalse("Duplicte receiver is allowed to register for the same message type.", commsMessageDispatcher.registerControlMessageReceiver(ControlMessage.Discriminator.DiscoveryRequest, duplicateReceiver));


    }

    class MockReceiver implements CtrlMsgReceiver {

        @Override
        public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
            switch (id) {
                case DiscoveryRequest:
                    logger.info("Received discovery request.");
                    requestReceived = true;
                    break;
                case DiscoveryResponse:
                    logger.info("Received discovery response.");
                    responseReceived = true;
                    break;
                default:
                    logger.error("Unknown control message > " + id);
                    unKnownMessageReceived = true;
                    return false;
            }
            return true;
        }
    }
}
