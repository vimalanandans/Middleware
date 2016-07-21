package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies the construction of different callback messages by retrieving the callback type after deserialization.
 */
public class CallBackMessageTest {
    @Test
    public void testUnicastStreamCallbackMessage() {
        StreamIncomingMessage streamIncomingMessage = new StreamIncomingMessage(null, null, null, null, (short) 0, null);
        assertEquals("STREAM_UNICAST", streamIncomingMessage.getCallbackType());

        String serializedCallback = streamIncomingMessage.serialize();
        StreamIncomingMessage deserializedCallbackMessage = StreamIncomingMessage.deserialize(serializedCallback, StreamIncomingMessage.class);
        assertEquals("STREAM_UNICAST", deserializedCallbackMessage.getCallbackType());
    }

    @Test
    public void testStreamStatusCallbackMessage() {
        StreamStatusMessage streamStatusMessage = new com.bezirk.proxy.messagehandler.StreamStatusMessage(null, 0, (short) 0);
        assertEquals("STREAM_STATUS", streamStatusMessage.getCallbackType());

        String serializedCallback = streamStatusMessage.serialize();
        StreamStatusMessage deserializedCallbackMessage = StreamStatusMessage.deserialize(serializedCallback, StreamStatusMessage.class);
        assertEquals("STREAM_STATUS", deserializedCallbackMessage.getCallbackType());

    }

    @Test
    public void testEventCallBackMessage() {
        ZirkId recipientId = new ZirkId("TestService");
        BezirkZirkEndPoint senderSEP = new BezirkZirkEndPoint(new ZirkId("SenderServiceID"));
        String serializedEvent = "TestEvent";
        String eventTopic = "TestTopic";
        String msgId = "1234";
        EventIncomingMessage eventIncomingMessage = new com.bezirk.proxy.messagehandler.EventIncomingMessage(recipientId, senderSEP, serializedEvent, eventTopic, msgId);
        assertEquals("EVENT", eventIncomingMessage.getCallbackType());

        String serializedCallback = eventIncomingMessage.serialize();
        EventIncomingMessage deserializedCallbackMessage = EventIncomingMessage.deserialize(serializedCallback, EventIncomingMessage.class);
        assertEquals("EVENT", deserializedCallbackMessage.getCallbackType());
        assertEquals(serializedEvent, deserializedCallbackMessage.getSerializedEvent());
        assertEquals(eventTopic, deserializedCallbackMessage.getEventTopic());
        assertEquals(msgId, deserializedCallbackMessage.getMsgId());
    }

    @Test
    public void testDiscoveryCallBackMessage() {
        DiscoveryIncomingMessage discoveryCallBackMessage = new DiscoveryIncomingMessage(null, null, 0, true);
        assertEquals("DISCOVERY", discoveryCallBackMessage.getCallbackType());

        String serializedCallback = discoveryCallBackMessage.serialize();
        DiscoveryIncomingMessage deserializedCallbackMessage = DiscoveryIncomingMessage.deserialize(serializedCallback, DiscoveryIncomingMessage.class);
        assertEquals("DISCOVERY", deserializedCallbackMessage.getCallbackType());
        assertTrue(deserializedCallbackMessage.isSphereDiscovery());
    }
}
