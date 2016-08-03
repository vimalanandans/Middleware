package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

public class UnicastHeader extends Header {
    private BezirkZirkEndPoint recipient;

    public BezirkZirkEndPoint getRecipient() {
        return recipient;
    }

    public void setRecipient(BezirkZirkEndPoint recipient) {
        this.recipient = recipient;
    }
}
