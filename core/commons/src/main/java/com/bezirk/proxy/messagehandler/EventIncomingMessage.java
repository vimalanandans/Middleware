package com.bezirk.proxy.messagehandler;

import com.bezirk.actions.BezirkAction;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;


/**
 * Sub class of ServiceIncomingMessage that contains the EventCallbackMessage fields, that are
 * required for ProxyForBezirk to give the callback.
 */
public final class EventIncomingMessage extends ServiceIncomingMessage {
    private final BezirkZirkEndPoint senderEndPoint;
    private final String serializedEvent;
    private final String eventTopic;
    /**
     * unique msg id for each event. This is useful to avoid duplicates at ProxyForBezirk side when
     * a zirk is residing in multiple spheres.
     */
    private final String msgId;

    public EventIncomingMessage(ZirkId recipientId, BezirkZirkEndPoint senderEndPoint, String serializedEvent, String eventTopic, String msgId) {
        super(BezirkAction.ACTION_ZIRK_RECEIVE_EVENT, recipientId);

        this.senderEndPoint = senderEndPoint;
        this.serializedEvent = serializedEvent;
        this.eventTopic = eventTopic;
        this.msgId = msgId;
    }

    public BezirkZirkEndPoint getSenderEndPoint() {
        return senderEndPoint;
    }

    public String getSerializedEvent() {
        return serializedEvent;
    }

    public String getEventTopic() {
        return eventTopic;
    }

    public String getMsgId() {
        return msgId;
    }
}
