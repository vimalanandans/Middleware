package com.bezirk.actions;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.proxy.api.impl.ZirkId;

public class SendUnicastEventAction extends EventAction {
    private final ZirkEndPoint recipient;

    public SendUnicastEventAction(ZirkId zirkId, ZirkEndPoint recipient, Event event) {
        super(zirkId, event);

        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send an event to a null recipient. You " +
                    "probably want to use sendEvent(Event)");
        }

        this.recipient = recipient;
    }

    public ZirkEndPoint getRecipient() {
        return recipient;
    }

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_ZIRK_SEND_UNICAST_EVENT;
    }
}
