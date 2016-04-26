package com.bezirk.control.messages.pipes;

import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

public class PipeUnicastHeader extends PipeHeader {

    private BezirkZirkEndPoint recipient;

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

    public BezirkZirkEndPoint getRecipient() {
        return recipient;
    }

    public void setRecipient(BezirkZirkEndPoint recipient) {
        this.recipient = recipient;
    }

}
