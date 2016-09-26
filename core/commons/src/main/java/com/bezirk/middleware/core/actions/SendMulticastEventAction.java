package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import java.io.Serializable;

public class SendMulticastEventAction extends EventAction {

    private static final long serialVersionUID = 5208841186626658098L;
    private final RecipientSelector selector;
    private boolean isIdentified = false;
    private Alias alias;

    public SendMulticastEventAction(ZirkId zirkId, RecipientSelector selector, Event event) {
        super(zirkId, event);

        if (selector == null) {
            throw new IllegalArgumentException("Cannot send an event to a null recipient. You " +
                    "probably want to use sendEvent(Event)");
        }

        this.selector = selector;
    }

    public SendMulticastEventAction(ZirkId zirkId, RecipientSelector selector, Event event,
                                    boolean isIdentified) {
        this(zirkId, selector, event);

        this.isIdentified = isIdentified;
    }

    public RecipientSelector getRecipientSelector() {
        return selector;
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
        return BezirkAction.ACTION_ZIRK_SEND_MULTICAST_EVENT;
    }
}
