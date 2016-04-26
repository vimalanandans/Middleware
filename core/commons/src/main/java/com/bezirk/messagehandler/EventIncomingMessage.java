package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;


/**
 * Sub class of UhuCallbackMessage that contains the EventCallbackMessage fields, that are required for ProxyForUhu to give the callback.
 */
public final class EventIncomingMessage extends ServiceIncomingMessage {

    /**
     * UhuZirkEndPoint of the the recipient.
     */
    public UhuZirkEndPoint senderSEP;
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

    public EventIncomingMessage(UhuZirkId recipientId, UhuZirkEndPoint senderSEP, String serialzedEvent, String eventTopic, String msgId) {
        super();
        callbackDiscriminator = "EVENT";
        recipient = recipientId;
        this.senderSEP = senderSEP;
        this.serialzedEvent = serialzedEvent;
        this.eventTopic = eventTopic;
        this.msgId = msgId;
    }
}
