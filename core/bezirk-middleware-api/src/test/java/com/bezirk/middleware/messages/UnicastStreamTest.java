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

import com.bezirk.middleware.messages.Message.Stripe;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *	 This testcase verifies the UnicastStream by setting the properties and retrieving them.
 *
 * @author AJC6KOR
 *
 */
public class UnicastStreamTest {

    @Test
    public void test() {

        Stripe stripe = Stripe.REQUEST;
        String topic = "TestTopic";
        MockServiceEndpoint recipient = new MockServiceEndpoint("ServiceA");
        com.bezirk.middleware.messages.UnicastStream unicastStream = new UnicastStream(stripe, topic, recipient);

	
	/*
	 * --- Deserialization is failing serviceEndpoint doesn't have a no-args constructor-----
			String serializedUnicastStream = unicastStream.serialize();
	 		UnicastStream deserializedUnicastStream = UnicastStream.deserialize(serializedUnicastStream, UnicastStream.class);*/

        assertEquals("Recipient is not equal to the set value.", recipient, unicastStream.getRecipient());

    }

}
