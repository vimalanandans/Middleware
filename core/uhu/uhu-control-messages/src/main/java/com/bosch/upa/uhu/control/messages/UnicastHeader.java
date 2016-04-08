package com.bosch.upa.uhu.control.messages;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;

/**
 * Created by ANM1PI on 6/16/2014.
 */
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
