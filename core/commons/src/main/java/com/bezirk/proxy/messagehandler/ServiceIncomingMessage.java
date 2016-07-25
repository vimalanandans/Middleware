package com.bezirk.proxy.messagehandler;

import com.bezirk.actions.BezirkAction;
import com.bezirk.proxy.api.impl.ZirkId;
import com.google.gson.Gson;

import java.io.Serializable;

public class ServiceIncomingMessage implements Serializable {
    private final BezirkAction action;
    private final ZirkId recipient;

    public ServiceIncomingMessage(BezirkAction action, ZirkId recipient) {
        this.action = action;
        this.recipient = recipient;
    }

    public BezirkAction getAction() {
        return action;
    }

    public ZirkId getRecipient() {
        return recipient;
    }
}
