/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 * <p/>
 * Authors: Joao de Sousa, 2014
 * Mansimar Aneja, 2014
 * Vijet Badigannavar, 2014
 * Samarjit Das, 2014
 * Cory Henson, 2014
 * Sunil Kumar Meena, 2014
 * Adam Wynne, 2014
 * Jan Zibuschka, 2014
 */
package com.bezirk.middleware.messages;

import com.bezirk.middleware.messages.Message.Flag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the Stream by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class StreamTest {

    @Test
    public void test() {

        Flag flag = Message.Flag.REPLY;
        String topic = "TestTopic";

        boolean allowDrops = false;
        boolean incremental = false;
        boolean secure = true;

        com.bezirk.middleware.messages.Stream stream = new MockStream(flag, topic);
        stream.setAllowDrops(allowDrops);
        stream.setIncremental(incremental);
        stream.setEncrypted(secure);

        String serializedStream = stream.serialize();

        Stream deserializedStream = Stream.deserialize(serializedStream, MockStream.class);

        assertEquals("Flag is not equal to the set value.", flag, deserializedStream.flag);
        assertEquals("Topic is not equal to the set value.", topic, deserializedStream.topic);
        assertFalse("AllowDrops is not equal to the set value.", deserializedStream.isAllowDrops());
        assertFalse("Incremental is not equal to the set value.", deserializedStream.isIncremental());
        assertTrue("Secure is not equal to the set value.", deserializedStream.isEncrypted());
    }

    private class MockStream extends Stream {
        MockStream(Flag flag, String topic) {
            super(flag, topic);
        }
    }
}
