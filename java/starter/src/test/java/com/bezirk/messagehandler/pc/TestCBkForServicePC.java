package com.bezirk.messagehandler.pc;

import com.bezirk.proxy.messagehandler.BroadcastReceiver;
import com.bezirk.proxy.messagehandler.ServiceMessageHandler;
import com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.ServiceIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class TestCBkForServicePC {
    private static final Logger logger = LoggerFactory.getLogger(TestCBkForServicePC.class);

    private final BroadcastReceiver BRForService = new BRForServiceMock();
    private final ServiceMessageHandler cBkForServicePC = new ServiceMessageHandler(BRForService);
    private boolean receivedEvent = false;
    private boolean receivedUnicastStream = false;
    private boolean receivedStreamStatus = false;

    @Test
    public void testFireEventCallback() {
        EventIncomingMessage eventCallbackMessage = new EventIncomingMessage();
        cBkForServicePC.onIncomingEvent(eventCallbackMessage);

        assertTrue("Callback Zirk is unable to fire eventCallback. ", receivedEvent);
    }

    @Test
    public void testFireUnicastStreamCallback() {
        StreamIncomingMessage unicastStreamCallbackMessage = new StreamIncomingMessage();
        cBkForServicePC.onIncomingStream(unicastStreamCallbackMessage);

        assertTrue("Callback Zirk is unable to fire Unicast stream.", receivedUnicastStream);

    }

    @Test
    public void testFireStreamStatusCallback() {
        StreamStatusMessage streamStatusCallbackMessage = new StreamStatusMessage();
        cBkForServicePC.onStreamStatus(streamStatusCallbackMessage);

        assertTrue("Callback Zirk is unable to fire stream status.", receivedStreamStatus);

    }

    private enum CallBackDiscriminator {
        EVENT, STREAM_UNICAST, STREAM_STATUS
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
            }
        }


    }
}