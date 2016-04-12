package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;


/**
 * Sub class of UhuCallbackMessage that contains the EventCallbackMessage fields, that are required for ProxyForUhu to give the callback. 
 */
public final class EventIncomingMessage extends ServiceIncomingMessage {

	/**
	 * UhuServiceEndPoint of the the recipient.
	 */
	public UhuServiceEndPoint senderSEP;
	/**
	 * Serialized Topic
	 */
	public String serialzedEvent;
	/**
	 * Name of the Event topic
	 */
	public String eventTopic;
	/**
	 * unique msg id for each event. This is useful to avoid duplicates at ProxyForUhu side when a service is residing in multiple spheres.
	 */
	public String msgId;
	
	public EventIncomingMessage() {
		callbackDiscriminator = "EVENT";
	}

	public EventIncomingMessage(UhuServiceId recipientId, UhuServiceEndPoint senderSEP, String serialzedEvent, String eventTopic, String msgId) {
		super();
		callbackDiscriminator = "EVENT";
		recipient = recipientId;
		this.senderSEP = senderSEP;
		this.serialzedEvent = serialzedEvent;
		this.eventTopic = eventTopic;
		this.msgId = msgId;
	}
}
