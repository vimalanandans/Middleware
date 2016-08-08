package com.bezirk.control.messages;

import com.bezirk.middleware.addressing.RecipientSelector;

public class MulticastHeader extends Header {
    private RecipientSelector recipientSelector;

    public RecipientSelector getRecipientSelector() {
        return recipientSelector;
    }

    public void setRecipientSelector(RecipientSelector recipientSelector) {
        this.recipientSelector = recipientSelector;
    }
}
