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
 * Protocol definitions must extend this class to define concrete streams, e.g. by adding message-specific attributes.
 * In other words, streams are messages followed by a large amount of data.
 * Extends {@link Message}
 */
public class Stream extends Message {
	/**
	 * Set by each subclass, it tells UhU whether the stream should be processed incrementally (e.g. a music stream) or as a unit (e.g. an image file)
	 */
	private boolean incremental;
	/**
	 * Set by each subclass, it tells UhU whether the stream allows data to be dropped, thereby promoting real-time delivery
	 */
	private boolean allowDrops;
	
	/**
	 * Set by each subclass, it tells UhU whether the stream MUST be encrypted while in transit in the Sphere.
	 * The user may dynamically instruct UhU via UI whether to encrypt the exchange of streams marked unsecured: a tradeoff between privacy and performance.
	 */
	private boolean secure;
	
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