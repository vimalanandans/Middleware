package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;


/**
 * Sub class of ServiceIncomingMessage that contains the EventCallbackMessage fields, that are
 * required for ProxyForBezirk to give the callback.
 */
public final class EventIncomingMessage extends ServiceIncomingMessage {

    /**
     * BezirkZirkEndPoint of the the recipient.
     */
    public BezirkZirkEndPoint senderSEP;
    /**
     * Serialized Topic
     */
    public String serializedEvent;
    /**
     * Name of the Event topic
     */
    public String eventTopic;
    /**
     * unique msg id for each event. This is useful to avoid duplicates at ProxyForBezirk side when
     * a zirk is residing in multiple spheres.
     */
    public String msgId;

    public EventIncomingMessage() {
        callbackDiscriminator = "EVENT";
    }

    public EventIncomingMessage(BezirkZirkId recipientId, BezirkZirkEndPoint senderSEP, String serializedEvent, String eventTopic, String msgId) {
        super();
        callbackDiscriminator = "EVENT";
        recipient = recipientId;
        this.senderSEP = senderSEP;
        this.serializedEvent = serializedEvent;
        this.eventTopic = eventTopic;
        this.msgId = msgId;
    }
}
