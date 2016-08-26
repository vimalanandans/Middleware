package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

public class UnicastEventAction extends EventAction {
    private final BezirkAction action;
    private final ZirkEndPoint endpoint;
    private boolean isIdentified = false;
    private Alias alias;

    public UnicastEventAction(BezirkAction action, ZirkId zirkId, ZirkEndPoint endpoint, Event event) {
        super(zirkId, event);

        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint must not be null");
        }

        this.action = action;
        this.endpoint = endpoint;
    }

    public UnicastEventAction(BezirkAction action, ZirkId zirkId, ZirkEndPoint endpoint, Event event,
                              boolean isIdentified) {
        this(action, zirkId, endpoint, event);

        this.isIdentified = isIdentified;
    }

    public UnicastEventAction(BezirkAction action, ZirkId zirkId, ZirkEndPoint endpoint,
                             String serializedEvent, String messageId, String eventName) {
        super(zirkId, serializedEvent, messageId, eventName);

        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint must not be null");
        }

        this.action = action;
        this.endpoint = endpoint;
    }

    public UnicastEventAction(BezirkAction action, ZirkId zirkId, ZirkEndPoint endpoint,
                              String serializedEvent, String messageId, String eventName,
                              boolean isIdentified) {
        this(action, zirkId, endpoint, serializedEvent, messageId, eventName);

        this.isIdentified = isIdentified;
    }

    public ZirkEndPoint getEndpoint() {
        return endpoint;
    }

    public boolean isIdentified() {
        return isIdentified;
    }

    public Alias getAlias() {
        return alias;
    }

    public void setAlias(Alias alias) {
        this.alias = alias;
    }

    @Override
    public BezirkAction getAction() {
        return action;
    }
}
