package com.bezirk.actions;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.proxy.api.impl.ZirkId;

public class UnicastEventAction extends EventAction {
    private final BezirkAction action;
    private final ZirkEndPoint endpoint;

    public UnicastEventAction(BezirkAction action, ZirkId zirkId, ZirkEndPoint endpoint, Event event) {
        super(zirkId, event);

        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint must not be null");
        }

        this.action = action;
        this.endpoint = endpoint;
    }

    public UnicastEventAction(BezirkAction action, ZirkId zirkId, ZirkEndPoint endpoint,
                              String topic, String serializedEvent, String messageId) {
        super(zirkId, topic, serializedEvent, messageId);

        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint must not be null");
        }

        this.action = action;
        this.endpoint = endpoint;
    }

    public ZirkEndPoint getEndpoint() {
        return endpoint;
    }

    @Override
    public BezirkAction getAction() {
        return action;
    }
}
