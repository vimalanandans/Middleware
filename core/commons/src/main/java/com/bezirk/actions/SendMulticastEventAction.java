package com.bezirk.actions;

import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.messages.Event;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.Serializable;

public class SendMulticastEventAction extends EventAction implements Serializable {
    private final RecipientSelector selector;

    public SendMulticastEventAction(ZirkId zirkId, RecipientSelector selector, Event event) {
        super(zirkId, event);

        if (selector == null) {
            throw new IllegalArgumentException("Cannot send an event to a null recipient. You " +
                    "probably want to use sendEvent(Event)");
        }

        this.selector = selector;
    }

    public RecipientSelector getRecipientSelector() {
        return selector;
    }

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_ZIRK_SEND_MULTICAST_EVENT;
    }
}
