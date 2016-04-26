package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;


/**
 * Sub class of UhuCallbackMessage that contains the EventCallbackMessage fields, that are required for ProxyForUhu to give the callback.
 */
public final class EventIncomingMessage extends ServiceIncomingMessage {

    /**
     * BezirkZirkEndPoint of the the recipient.
     */
    public BezirkZirkEndPoint senderSEP;
    /**
     * Serialized Topic
     */
    public String serialzedEvent;
    /**
     * Name of the Event topic
     */
    public String eventTopic;
    /**
     * unique msg id for each event. This is useful to avoid duplicates at ProxyForUhu side when a zirk is residing in multiple spheres.
     */
    public String msgId;

    public EventIncomingMessage() {
        callbackDiscriminator = "EVENT";
    }

    public EventIncomingMessage(BezirkZirkId recipientId, BezirkZirkEndPoint senderSEP, String serialzedEvent, String eventTopic, String msgId) {
        super();
        callbackDiscriminator = "EVENT";
        recipient = recipientId;
        this.senderSEP = senderSEP;
        this.serialzedEvent = serialzedEvent;
        this.eventTopic = eventTopic;
        this.msgId = msgId;
    }
}
