package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.UhuZirkEndPoint;

public class UnicastHeader extends Header {
    //TODO: Move UhuZirkEndPoint to Java-Common
    private UhuZirkEndPoint recipient;

    public UhuZirkEndPoint getRecipient() {
        return recipient;
    }

    public void setRecipient(UhuZirkEndPoint recipient) {
        this.recipient = recipient;
    }
}
