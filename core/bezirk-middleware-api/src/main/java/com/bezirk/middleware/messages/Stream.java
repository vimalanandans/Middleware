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
 * Base class for non-trivial Bezirk messages and data transfers. A stream represents a set of data
 * elements such as picture and music data. This class is extended by protocol implementations to 
 * define concrete streams and their custom attributes and payloads. To implement a simple, small
 * message, extend the {@link Event} class.
 * 
 * @see Message
 * @see Event
 */
public class Stream extends Message {
	/**
	 * Subclass sets to <code>true</code> if the payload can be processed incrementally (e.g. a music stream) or 
	 * <code>false</code> if all data elements must be received before processing can continue (e.g. image file 
	 * data).
	 */
	private boolean incremental;
	
	/**
	 * Subclass sets to <code>true</code> if data elements may be dropped from the stream without resending
	 * to increase performance, or <code>false</code> if the data elements must be reliably transferred.
	 */
	private boolean allowDrops;
	
	/**
	 * Subclass sets to <code>true</code> if Bezirk must encrypt the stream's data before transmitting. If
	 * set to <code>false</code>, Bezirk will offer the user the opportunity to encrypt the stream anyway. 
	 * This option is provided to allow users to make a tradeoff between privacy and performance where the 
	 * protocol designer does not believe the stream will always require confidentiality.
	 */
	private boolean secure;
	
	/**
	 * The concrete implentation of a <code>Stream</code> must specify the stream's flag
	 * and topic. Message flags and topics are documented in {@link Message}.
	 */
	public Stream(Stripe stripe, String topic){
		this.stripe = stripe;
		this.topic = topic;
	}

	public void setIncremental(boolean incremental) {
		this.incremental = incremental;
	}

	public void setAllowDrops(boolean allowDrops) {
		this.allowDrops = allowDrops;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	
	public boolean isIncremental() {
		return incremental;
	}

	public boolean isAllowDrops() {
		return allowDrops;
	}

	public boolean isSecure() {
		return secure;
	}
}
