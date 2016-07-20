package com.bezirk.middleware.messages;

import com.bezirk.middleware.messages.Message.Flag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StreamDescriptorTest {
    @Test
    public void test() {
        boolean incremental = false;
        boolean encrypted = true;

        StreamDescriptor streamDescriptor = new MockStreamDescriptor(incremental, encrypted);

        String serializedStream = streamDescriptor.toJson();

        StreamDescriptor deserializedStreamDescriptor = StreamDescriptor.fromJson(serializedStream, MockStreamDescriptor.class);

        assertFalse("Incremental is not equal to the set value.", deserializedStreamDescriptor.isIncremental());
        assertTrue("Secure is not equal to the set value.", deserializedStreamDescriptor.isEncrypted());
    }

    private class MockStreamDescriptor extends StreamDescriptor {
        MockStreamDescriptor(boolean incremental, boolean encrypted) {
            super(incremental, encrypted);
        }
    }
}
