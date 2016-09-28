package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.core.networking.NetworkManager;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.bezirk.middleware.core.streaming.control.Objects.StreamRecord;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

import static org.junit.Assert.assertFalse;

/**
 * This testcase tests the working of message dispatcher. A mock receiver is registered for different control messages with the message dispatcher.
 * Tests verifies that the control messages are properly routed to the receiver based on the discriminators registered wth message dispatcher.
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
//        inetAddr = getInetAddress();
//        recipient.device = inetAddr.getHostAddress();
    }

//    private static InetAddress getInetAddress() {
//        try {
//
//            for (Enumeration<NetworkInterface> en = NetworkInterface
//                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf
//                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
//
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress()
//                            && !inetAddress.isLinkLocalAddress()
//                            && inetAddress.isSiteLocalAddress()) {
//
//                        inetAddr = NetworkManager.getIpForInterface(intf);
//                        return inetAddr;
//                    }
//
//                }
//            }
//        } catch (SocketException e) {
//            logger.error("Unable to fetch network interface");
//        }
//        return null;
//    }


    @Test
    public void test() {
        Device device = Mockito.mock(Device.class);
        NetworkManager networkManager = Mockito.mock(NetworkManager.class);
        //PubSubEventReceiver bezirkSadlManager = new PubSubBroker(null,device, networkManager);
        com.bezirk.middleware.core.comms.CommsMessageDispatcher commsMessageDispatcher = new com.bezirk.middleware.core.comms.CommsMessageDispatcher();

        com.bezirk.middleware.core.comms.CtrlMsgReceiver receiver = new MockReceiver();
        commsMessageDispatcher.registerControlMessageReceiver(ControlMessage.Discriminator.DISCOVERY_REQUEST, receiver);

        ControlLedger tcMessage = new ControlLedger();
        StreamRecord record  = new StreamRecord();

        //// FIXME: 8/1/2016 set the proper record here, uncomment below lines
        /*ControlMessage streamRequest = new STREAM_REQUEST(null, record, null);
        tcMessage.setMessage(streamRequest);
        commsMessageDispatcher.dispatchControlMessages(tcMessage);*/

        // FIXME: 8/1/2016 Punith. Fix the test case for the changes to STREAM_REQUEST constructor.
        //assertFalse("Unknown Message type is recieved by mock receiver.", unKnownMessageReceived);

        com.bezirk.middleware.core.comms.CtrlMsgReceiver duplicateReceiver = new MockReceiver();
        //assertFalse("Duplicte receiver is allowed to register for the same message type.", commsMessageDispatcher.registerControlMessageReceiver(ControlMessage.Discriminator.DISCOVERY_REQUEST, duplicateReceiver));


    }

    class MockReceiver implements CtrlMsgReceiver {

        @Override
        public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
            switch (id) {
                case DISCOVERY_REQUEST:
                    logger.info("Received discovery request.");
                    requestReceived = true;
                    break;
                case DISCOVERY_RESPONSE:
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
