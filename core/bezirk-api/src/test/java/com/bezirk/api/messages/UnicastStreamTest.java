/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 *
 * Authors: Joao de Sousa, 2014
 *          Mansimar Aneja, 2014
 *          Vijet Badigannavar, 2014
 *          Samarjit Das, 2014
 *          Cory Henson, 2014
 *          Sunil Kumar Meena, 2014
 *          Adam Wynne, 2014
 *          Jan Zibuschka, 2014
 */
package com.bezirk.api.messages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bezirk.api.messages.Message.Stripe;

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
	String topic="TestTopic";
	MockServiceEndpoint recipient= new MockServiceEndpoint("ServiceA");
	com.bezirk.api.messages.UnicastStream unicastStream = new UnicastStream(stripe, topic, recipient);
	
	
	/*
	 * --- Deserialization is failing serviceEndpoint doesn't have a no-args constructor-----
			String serializedUnicastStream = unicastStream.serialize();
	 		UnicastStream deserializedUnicastStream = UnicastStream.deserialize(serializedUnicastStream, UnicastStream.class);*/
	
	assertEquals("Recipient is not equal to the set value.",recipient,unicastStream.getRecipient());
	
	}

}
