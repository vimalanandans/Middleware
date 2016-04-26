package com.bezirk.control.messages.pipes;

import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.proxy.api.impl.UhuZirkEndPoint;

public class PipeUnicastHeader extends PipeHeader {

    private UhuZirkEndPoint recipient;

    public UnicastHeader toUnicastHeader() {
        UnicastHeader unicastHeader = new UnicastHeader();
        unicastHeader.setRecipient(this.getRecipient());
        unicastHeader.setSenderSEP(this.getSenderSEP());
        unicastHeader.setTopic(this.getTopic());

        // TODO: How to set these??
        //unicastHeader.setMessageId(?);
        //unicastHeader.setSphereName(?);

        return unicastHeader;
    }

    public UhuZirkEndPoint getRecipient() {
        return recipient;
    }

    public void setRecipient(UhuZirkEndPoint recipient) {
        this.recipient = recipient;
    }

}
