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

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Message.Flag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the UnicastStream by setting the properties and retrieving them.
 *
 * @author AJC6KOR
 */
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
