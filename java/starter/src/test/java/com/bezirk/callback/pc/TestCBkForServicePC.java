package com.bezirk.callback.pc;

import com.bezirk.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.messagehandler.EventIncomingMessage;
import com.bezirk.messagehandler.PipeRequestIncomingMessage;
import com.bezirk.messagehandler.ServiceIncomingMessage;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.messagehandler.StreamStatusMessage;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class TestCBkForServicePC {
    private static final Logger logger = LoggerFactory.getLogger(TestCBkForServicePC.class);

    private final BroadcastReceiver BRForService = new BRForServiceMock();
    private final CBkForZirkPC cBkForServicePC = new CBkForZirkPC(BRForService);
    private boolean receivedEvent = false;
    private boolean receivedUnicastStream = false;
    private boolean receivedStreamStatus = false;
    private boolean receivedDiscovery = false;

    @BeforeClass
    public static void setUpClass() {
        logger.info("********** Setting up TestCBkForServicePC Testcase **********");
    }


    @AfterClass
    public static void tearDownClass() {
        logger.info("********** Shutting down TestCBkForServicePC Testcase **********");
    }

    @Test
    public void test() {

        testFireEventCallback();

        testFireUnicastStreamCallback();

        testFireStreamStatusCallback();

        testFireDiscoveryCallback();

    }

    private void testFireEventCallback() {
        EventIncomingMessage eventCallbackMessage = new EventIncomingMessage();
        cBkForServicePC.onIncomingEvent(eventCallbackMessage);

        assertTrue("Callback Zirk is unable to fire eventCallback. ", receivedEvent);
    }

    private void testFireUnicastStreamCallback() {
        StreamIncomingMessage unicastStreamCallbackMessage = new StreamIncomingMessage();
        cBkForServicePC.onIncomingStream(unicastStreamCallbackMessage);

        assertTrue("Callback Zirk is unable to fire Unicast stream.", receivedUnicastStream);

    }

    @Test(expected = RuntimeException.class)
    public void testFirePipeApprovedCallBack() {
        PipeRequestIncomingMessage pipeMsg = new PipeRequestIncomingMessage();
        cBkForServicePC.onPipeApprovedMessage(pipeMsg);
    }

    private void testFireStreamStatusCallback() {
        StreamStatusMessage streamStatusCallbackMessage = new StreamStatusMessage();
        cBkForServicePC.onStreamStatus(streamStatusCallbackMessage);

        assertTrue("Callback Zirk is unable to fire stream status.", receivedStreamStatus);

    }

    private void testFireDiscoveryCallback() {
        DiscoveryIncomingMessage discoveryCallbackMessage = new DiscoveryIncomingMessage();
        cBkForServicePC.onDiscoveryIncomingMessage(discoveryCallbackMessage);

        assertTrue("Callback Zirk is unable to fire Discovery callback.", receivedDiscovery);

    }

    private enum CallBackDiscriminator {

        EVENT, STREAM_UNICAST, STREAM_STATUS, DISCOVERY;

    }

    class BRForServiceMock implements BroadcastReceiver {

        @Override
        public void onReceive(ServiceIncomingMessage callbackMessage) {

            if (callbackMessage.getCallbackType().equalsIgnoreCase(CallBackDiscriminator.EVENT.name())) {

                receivedEvent = true;

            } else if (callbackMessage.getCallbackType().equalsIgnoreCase(CallBackDiscriminator.STREAM_UNICAST.name())) {

                receivedUnicastStream = true;

            } else if (callbackMessage.getCallbackType().equalsIgnoreCase(CallBackDiscriminator.STREAM_STATUS.name())) {

                receivedStreamStatus = true;
            } else if (callbackMessage.getCallbackType().equalsIgnoreCase(CallBackDiscriminator.DISCOVERY.name())) {

                receivedDiscovery = true;
            }
        }


    }
}