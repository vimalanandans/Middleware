package com.bezirk.control.messages;

import com.bezirk.middleware.addressing.RecipientSelector;

/**
 * This Class reflects the Header for Multicast Events
 * Created by ANM1PI on 6/16/2014.
 */
public class MulticastHeader extends Header {
    private RecipientSelector recipientSelector;

    public RecipientSelector getRecipientSelector() {
        return recipientSelector;
    }

    public void setRecipientSelector(RecipientSelector recipientSelector) {
        this.recipientSelector = recipientSelector;
    }
}
