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

/**
 * Protocol definitions must extend this class to define concrete events, e.g. by adding a payload with message-specific attributes.
 * Extends {@link Message}
 */

public class Event extends Message {
	
	public Event(Stripe stripe, String topic){
		this.stripe = stripe;
		this.topic = topic;
	}
}
