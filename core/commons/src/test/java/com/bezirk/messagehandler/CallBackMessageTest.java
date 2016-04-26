package com.bezirk.messagehandler;

import com.bezirk.middleware.addressing.CloudPipe;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.pipe.policy.ext.UhuPipePolicy;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * This testcase verifies the construction of different callback messages by retrieving the callback type after deserialization.
 *
 * @author AJC6KOR
 */
public class CallBackMessageTest {

    @Test
    public void test() {

        testDiscoveryCallBackMessage();

        testEventCallBackMessage();

        testMulticastCallbackMessage();

        testPipeRequestCallbackMessage();

        testStreamStatusCallbackMessage();

        testUnicastStreamCallbackMessage();

    }

    private void testUnicastStreamCallbackMessage() {
        StreamIncomingMessage streamIncomingMessage = new StreamIncomingMessage();
        assertEquals("Callbackdiscriminator is not set to STREAM_UNICAST for unicastStreamCallbackMessage.", "STREAM_UNICAST", streamIncomingMessage.callbackDiscriminator);

        String serializedCallback = streamIncomingMessage.serialize();
        StreamIncomingMessage deserializedcallbackMessage = StreamIncomingMessage.deserialize(serializedCallback, StreamIncomingMessage.class);
        assertEquals("Callbackdiscriminator is not set to STREAM_UNICAST for unicastStreamCallbackMessage.", "STREAM_UNICAST", deserializedcallbackMessage.getCallbackType());

        streamIncomingMessage = new StreamIncomingMessage(null, null, null, null, (short) 0, null);
        assertEquals("Callbackdiscriminator is not set to STREAM_UNICAST for unicastStreamCallbackMessage.", "STREAM_UNICAST", streamIncomingMessage.callbackDiscriminator);

    }

    private void testStreamStatusCallbackMessage() {
        StreamStatusMessage streamStatusMessage = new StreamStatusMessage();
        assertEquals("Callbackdiscriminator is not set to STREAM_STATUS for streamStatusCallbackMessage.", "STREAM_STATUS", streamStatusMessage.callbackDiscriminator);

        String serializedCallback = streamStatusMessage.serialize();
        StreamStatusMessage deserializedcallbackMessage = StreamStatusMessage.deserialize(serializedCallback, StreamStatusMessage.class);
        assertEquals("Callbackdiscriminator is not set to STREAM_STATUS for streamStatusCallbackMessage.", "STREAM_STATUS", deserializedcallbackMessage.getCallbackType());

        streamStatusMessage = new StreamStatusMessage(null, 0, (short) 0);
        assertEquals("Callbackdiscriminator is not set to STREAM_STATUS for streamStatusCallbackMessage.", "STREAM_STATUS", streamStatusMessage.callbackDiscriminator);
    }

    private void testPipeRequestCallbackMessage() {
        PipeRequestIncomingMessage pipeRequestIncomingMessage = new PipeRequestIncomingMessage();
        assertEquals("Callbackdiscriminator is not set to PIPE-APPROVED for pipeRequestCallbackMessage.", "PIPE-APPROVED", pipeRequestIncomingMessage.callbackDiscriminator);

        String serializedCallback = pipeRequestIncomingMessage.serialize();
        PipeRequestIncomingMessage deserializedcallbackMessage = PipeRequestIncomingMessage.deserialize(serializedCallback, PipeRequestIncomingMessage.class);
        assertEquals("Callbackdiscriminator is not set to PIPE-APPROVED for pipeRequestCallbackMessage.", "PIPE-APPROVED", deserializedcallbackMessage.getCallbackType());

        Pipe pipe = null;
        try {
            pipe = new CloudPipe("test", new URI("http;//test.com"));
        } catch (URISyntaxException e) {
            fail("Failed to create cloudpipe");
        }
        PipePolicy allowedIn = new MockPipePolicy();
        UhuPipePolicy allowedInPolicy = new UhuPipePolicy(allowedIn);
        PipePolicy allowedOut = new MockPipePolicy();
        UhuPipePolicy allowedOutPolicy = new UhuPipePolicy(allowedOut);
        String pipeReqId = "Request123";
        UhuServiceId recipient = new UhuServiceId("TestService");

        pipeRequestIncomingMessage = new PipeRequestIncomingMessage(pipe, pipeReqId, allowedInPolicy, allowedOutPolicy, recipient);

        assertEquals("AllowedInPolicy is not equal to the set value in pipeRequestCallbackMessage.", allowedInPolicy, pipeRequestIncomingMessage.getAllowedIn());
        assertEquals("AllowedOutPolicy is not equal to the set value in pipeRequestCallbackMessage.", allowedOutPolicy, pipeRequestIncomingMessage.getAllowedOut());
        assertEquals("Pipe is not equal to the set value in pipeRequestCallbackMessage.", pipe, pipeRequestIncomingMessage.getPipe());
        assertEquals("PipeReqId is not equal to the set value in pipeRequestCallbackMessage.", pipeReqId, pipeRequestIncomingMessage.getPipeReqId());
        assertEquals("Recepient is not equal to the set value in pipeRequestCallbackMessage.", recipient, pipeRequestIncomingMessage.getRecipient());
    }

    private void testMulticastCallbackMessage() {
        MulticastCallbackMessage multicastCallbackMessage = new MulticastCallbackMessage();
        assertEquals("Callbackdiscriminator is not set to MULTICAST_STREAM for multicastCallbackMessage.", "MULTICAST_STREAM", multicastCallbackMessage.callbackDiscriminator);

        String serializedCallback = multicastCallbackMessage.serialize();
        MulticastCallbackMessage deserializedcallbackMessage = MulticastCallbackMessage.deserialize(serializedCallback, MulticastCallbackMessage.class);
        assertEquals("Callbackdiscriminator is not set to MULTICAST_STREAM for multicastCallbackMessage.", "MULTICAST_STREAM", deserializedcallbackMessage.getCallbackType());
    }

    private void testEventCallBackMessage() {
        EventIncomingMessage eventIncomingMessage = new EventIncomingMessage();
        assertEquals("Callbackdiscriminator is not set to EVENT for eventCallbackMessage.", "EVENT", eventIncomingMessage.callbackDiscriminator);

        String serializedCallback = eventIncomingMessage.serialize();
        EventIncomingMessage deserializedcallbackMessage = EventIncomingMessage.deserialize(serializedCallback, EventIncomingMessage.class);
        assertEquals("Callbackdiscriminator is not set to EVENT for eventCallbackMessage.", "EVENT", deserializedcallbackMessage.getCallbackType());

        UhuServiceId recipientId = new UhuServiceId("TestService");
        UhuServiceEndPoint senderSEP = new UhuServiceEndPoint(new UhuServiceId("SenderServiceID"));
        String serialzedEvent = "TestEvent";
        String eventTopic = "TestTopic";
        String msgId = "1234";
        eventIncomingMessage = new EventIncomingMessage(recipientId, senderSEP, serialzedEvent, eventTopic, msgId);
        assertEquals("Callbackdiscriminator is not set to EVENT for eventCallbackMessage.", "EVENT", eventIncomingMessage.callbackDiscriminator);

        serializedCallback = eventIncomingMessage.serialize();
        deserializedcallbackMessage = EventIncomingMessage.deserialize(serializedCallback, EventIncomingMessage.class);
        assertEquals("Callbackdiscriminator is not set to EVENT for eventCallbackMessage.", "EVENT", deserializedcallbackMessage.getCallbackType());
    }

    private void testDiscoveryCallBackMessage() {
        DiscoveryIncomingMessage discoveryCallBackMessage = new DiscoveryIncomingMessage();
        assertEquals("Callbackdiscriminator is not set to DISCOVERY for discoverycallbackmessage.", "DISCOVERY", discoveryCallBackMessage.callbackDiscriminator);

        String serializedCallback = discoveryCallBackMessage.serialize();
        DiscoveryIncomingMessage deserializedcallbackMessage = DiscoveryIncomingMessage.deserialize(serializedCallback, DiscoveryIncomingMessage.class);
        assertEquals("Callbackdiscriminator is not set to DISCOVERY for discoveryCallBackMessage.", "DISCOVERY", deserializedcallbackMessage.getCallbackType());

        discoveryCallBackMessage = new DiscoveryIncomingMessage(null, null, 0, true);
        assertEquals("Callbackdiscriminator is not set to DISCOVERY for discoverycallbackmessage.", "DISCOVERY", discoveryCallBackMessage.callbackDiscriminator);
    }

    private class MockPipePolicy extends PipePolicy {
        public boolean isAuthorized(String protocolRoleName) {
            return false;
        }
    }
}
