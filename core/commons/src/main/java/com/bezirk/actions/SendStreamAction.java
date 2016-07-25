package com.bezirk.actions;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.api.impl.ZirkId;

public class SendStreamAction extends ZirkAction {
    private final ZirkEndPoint recipient;
    private final StreamDescriptor descriptor;
    private final short streamId;

    public SendStreamAction(ZirkId zirkId, ZirkEndPoint recipient, StreamDescriptor descriptor, short streamId) {
        super(zirkId);

        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send a streamDescriptor to a null recipient");
        }

        if (descriptor == null || descriptor.topic.isEmpty()) {
            throw new IllegalArgumentException("Null or empty streamDescriptor specified when sending " +
                    "a file");
        }

        this.recipient = recipient;
        this.descriptor = descriptor;
        this.streamId = streamId;
    }

    public ZirkEndPoint getRecipient() {
        return recipient;
    }

    public StreamDescriptor getDescriptor() {
        return descriptor;
    }

    public short getStreamId() {
        return streamId;
    }
}
