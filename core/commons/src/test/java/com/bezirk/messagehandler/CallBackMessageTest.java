package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies the construction of different callback messages by retrieving the callback type after deserialization.
 */
public class CallBackMessageTest {

    @Test
    public void test() {

        testDiscoveryCallBackMessage();

        testEventCallBackMessage();

        testStreamStatusCallbackMessage();

        testUnicastStreamCallbackMessage();

    }

    private void testUnicastStreamCallbackMessage() {
        com.bezirk.proxy.messagehandler.StreamIncomingMessage streamIncomingMessage = new com.bezirk.proxy.messagehandler.StreamIncomingMessage();
        assertEquals("Callbackdiscriminator is not set to STREAM_UNICAST for unicastStreamCallbackMessage.", "STREAM_UNICAST", streamIncomingMessage.callbackDiscriminator);

        String serializedCallback = streamIncomingMessage.serialize();
        com.bezirk.proxy.messagehandler.StreamIncomingMessage deserializedcallbackMessage = com.bezirk.proxy.messagehandler.StreamIncomingMessage.deserialize(serializedCallback, com.bezirk.proxy.messagehandler.StreamIncomingMessage.class);
        assertEquals("Callbackdiscriminator is not set to STREAM_UNICAST for unicastStreamCallbackMessage.", "STREAM_UNICAST", deserializedcallbackMessage.getCallbackType());

        streamIncomingMessage = new StreamIncomingMessage(null, null, null, null, (short) 0, null);
        assertEquals("Callbackdiscriminator is not set to STREAM_UNICAST for unicastStreamCallbackMessage.", "STREAM_UNICAST", streamIncomingMessage.callbackDiscriminator);

    }

    private void testStreamStatusCallbackMessage() {
        com.bezirk.proxy.messagehandler.StreamStatusMessage streamStatusMessage = new com.bezirk.proxy.messagehandler.StreamStatusMessage();
        assertEquals("Callbackdiscriminator is not set to STREAM_STATUS for streamStatusCallbackMessage.", "STREAM_STATUS", streamStatusMessage.callbackDiscriminator);

        String serializedCallback = streamStatusMessage.serialize();
        com.bezirk.proxy.messagehandler.StreamStatusMessage deserializedcallbackMessage = com.bezirk.proxy.messagehandler.StreamStatusMessage.deserialize(serializedCallback, com.bezirk.proxy.messagehandler.StreamStatusMessage.class);
        assertEquals("Callbackdiscriminator is not set to STREAM_STATUS for streamStatusCallbackMessage.", "STREAM_STATUS", deserializedcallbackMessage.getCallbackType());

        streamStatusMessage = new com.bezirk.proxy.messagehandler.StreamStatusMessage(null, 0, (short) 0);
        assertEquals("Callbackdiscriminator is not set to STREAM_STATUS for streamStatusCallbackMessage.", "STREAM_STATUS", streamStatusMessage.callbackDiscriminator);
    }

    private void testEventCallBackMessage() {
        com.bezirk.proxy.messagehandler.EventIncomingMessage eventIncomingMessage = new com.bezirk.proxy.messagehandler.EventIncomingMessage();
        assertEquals("Callbackdiscriminator is not set to EVENT for eventCallbackMessage.", "EVENT", eventIncomingMessage.callbackDiscriminator);

        String serializedCallback = eventIncomingMessage.serialize();
        com.bezirk.proxy.messagehandler.EventIncomingMessage deserializedcallbackMessage = com.bezirk.proxy.messagehandler.EventIncomingMessage.deserialize(serializedCallback, com.bezirk.proxy.messagehandler.EventIncomingMessage.class);
        assertEquals("Callbackdiscriminator is not set to EVENT for eventCallbackMessage.", "EVENT", deserializedcallbackMessage.getCallbackType());

        ZirkId recipientId = new ZirkId("TestService");
        BezirkZirkEndPoint senderSEP = new BezirkZirkEndPoint(new ZirkId("SenderServiceID"));
        String serialzedEvent = "TestEvent";
        String eventTopic = "TestTopic";
        String msgId = "1234";
        eventIncomingMessage = new com.bezirk.proxy.messagehandler.EventIncomingMessage(recipientId, senderSEP, serialzedEvent, eventTopic, msgId);
        assertEquals("Callbackdiscriminator is not set to EVENT for eventCallbackMessage.", "EVENT", eventIncomingMessage.callbackDiscriminator);

        serializedCallback = eventIncomingMessage.serialize();
        deserializedcallbackMessage = com.bezirk.proxy.messagehandler.EventIncomingMessage.deserialize(serializedCallback, EventIncomingMessage.class);
        assertEquals("Callbackdiscriminator is not set to EVENT for eventCallbackMessage.", "EVENT", deserializedcallbackMessage.getCallbackType());
    }

    private void testDiscoveryCallBackMessage() {
        com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage discoveryCallBackMessage = new com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage();
        assertEquals("Callbackdiscriminator is not set to DISCOVERY for discoverycallbackmessage.", "DISCOVERY", discoveryCallBackMessage.callbackDiscriminator);

        String serializedCallback = discoveryCallBackMessage.serialize();
        com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage deserializedcallbackMessage = com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage.deserialize(serializedCallback, com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage.class);
        assertEquals("Callbackdiscriminator is not set to DISCOVERY for discoveryCallBackMessage.", "DISCOVERY", deserializedcallbackMessage.getCallbackType());

        discoveryCallBackMessage = new DiscoveryIncomingMessage(null, null, 0, true);
        assertEquals("Callbackdiscriminator is not set to DISCOVERY for discoverycallbackmessage.", "DISCOVERY", discoveryCallBackMessage.callbackDiscriminator);
    }
}
