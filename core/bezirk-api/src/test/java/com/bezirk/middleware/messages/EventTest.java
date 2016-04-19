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
package com.bezirk.middleware.messages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bezirk.middleware.messages.Message.Stripe;

/**
 *	 This testcase verifies Event by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class EventTest {

	
	@Test
	public void test() {

		String topic = "TestEvent";
		Stripe stripe = Stripe.NOTICE;
		com.bezirk.middleware.messages.Event event = new Event(stripe, topic);
		
		String serializedEvent = event.serialize();
	
		Event deserializedEvent = Event.deserialize(serializedEvent, Event.class);
		
		assertEquals("Stripe is not equal to the set value.", stripe, deserializedEvent.stripe);
		assertEquals("Topic is not equal to the set value.",topic,deserializedEvent.topic);
	}

}
