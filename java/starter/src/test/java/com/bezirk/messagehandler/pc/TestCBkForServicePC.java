package com.bezirk.messagehandler.pc;

import com.bezirk.actions.BezirkAction;
import com.bezirk.actions.ZirkAction;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.messagehandler.BroadcastReceiver;
import com.bezirk.proxy.messagehandler.ServiceMessageHandler;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;

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
        EventIncomingMessage eventCallbackMessage = new EventIncomingMessage(new ZirkId("TEST"), null, null, null, null);
        cBkForServicePC.onIncomingEvent(eventCallbackMessage);

        assertTrue("Callback Zirk is unable to fire eventCallback. ", receivedEvent);
    }

    @Test
    public void testFireUnicastStreamCallback() {
        StreamIncomingMessage unicastStreamCallbackMessage = new StreamIncomingMessage(new ZirkId("TEST"), null, null, null, (short) 0, null);
        cBkForServicePC.onIncomingStream(unicastStreamCallbackMessage);

        assertTrue("Callback Zirk is unable to fire Unicast stream.", receivedUnicastStream);
    }

    @Test
    public void testFireStreamStatusCallback() {
        StreamStatusMessage streamStatusCallbackMessage = new StreamStatusMessage(new ZirkId("TEST"), 0, (short) 0);
        cBkForServicePC.onStreamStatus(streamStatusCallbackMessage);

        assertTrue("Callback Zirk is unable to fire stream status.", receivedStreamStatus);
    }

    class BRForServiceMock implements BroadcastReceiver {
        @Override
        public void onReceive(ZirkAction callbackMessage) {
            if (BezirkAction.ACTION_ZIRK_RECEIVE_EVENT.equals(callbackMessage.getAction())) {
                receivedEvent = true;
            } else if (BezirkAction.ACTION_ZIRK_RECEIVE_STREAM.equals(callbackMessage.getAction())) {
                receivedUnicastStream = true;
            } else if (BezirkAction.ACTION_ZIRK_RECEIVE_STREAM_STATUS.equals(callbackMessage.getAction())) {
                receivedStreamStatus = true;
            }
        }
    }
}