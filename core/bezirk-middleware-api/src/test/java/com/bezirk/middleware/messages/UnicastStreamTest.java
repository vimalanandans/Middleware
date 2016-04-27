package com.bezirk.middleware.messages;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Message.Flag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnicastStreamTest {
    @Test
    public void test() {
        Flag flag = Flag.REQUEST;
        String topic = "TestTopic";
        MockZirkEndpoint recipient = new MockZirkEndpoint("ZirkA");
        com.bezirk.middleware.messages.UnicastStream unicastStream = new MockUnicastStream(flag, topic, recipient);

        assertEquals("Recipient is not equal to the set value.", recipient, unicastStream.getRecipient());

    }

    private class MockUnicastStream extends UnicastStream {
        MockUnicastStream(Flag flag, String topic, ZirkEndPoint recipient) {
            super(flag, topic, recipient);
        }
    }
}
