package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.UhuServiceEndPoint;

public class UnicastHeader extends Header {
    //TODO: Move UhuServiceEndPoint to Java-Common
    private UhuServiceEndPoint recipient;

    public UhuServiceEndPoint getRecipient() {
        return recipient;
    }

    public void setRecipient(UhuServiceEndPoint recipient) {
        this.recipient = recipient;
    }
}
