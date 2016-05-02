package com.bezirk.middleware.messages;

import com.bezirk.middleware.messages.Message.Flag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StreamTest {
    @Test
    public void test() {
        Flag flag = Message.Flag.REPLY;
        String topic = "TestTopic";

        boolean allowDrops = false;
        boolean incremental = false;
        boolean secure = true;

        com.bezirk.middleware.messages.Stream stream = new MockStream(flag, topic);
        stream.setReliable(allowDrops);
        stream.setIncremental(incremental);
        stream.setEncrypted(secure);

        String serializedStream = stream.toJson();

        Stream deserializedStream = Stream.fromJson(serializedStream, MockStream.class);

        assertEquals("Flag is not equal to the set value.", flag, deserializedStream.flag);
        assertEquals("Topic is not equal to the set value.", topic, deserializedStream.topic);
        assertFalse("AllowDrops is not equal to the set value.", deserializedStream.isReliable());
        assertFalse("Incremental is not equal to the set value.", deserializedStream.isIncremental());
        assertTrue("Secure is not equal to the set value.", deserializedStream.isEncrypted());
    }

    private class MockStream extends Stream {
        MockStream(Flag flag, String topic) {
            super(flag, topic);
        }
    }
}
