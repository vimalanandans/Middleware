package com.bezirk.actions;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.api.impl.ZirkId;

public abstract class StreamAction extends ZirkAction {
    private final ZirkEndPoint recipient;
    private final StreamDescriptor descriptor;

    public StreamAction(ZirkId zirkId, ZirkEndPoint recipient, StreamDescriptor descriptor) {
        super(zirkId);

        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send a streamDescriptor to a null recipient");
        }

        if (descriptor == null) {
            throw new IllegalArgumentException("Null or empty streamDescriptor specified when sending " +
                    "a file");
        }

        this.recipient = recipient;
        this.descriptor = descriptor;
    }

    public ZirkEndPoint getRecipient() {
        return recipient;
    }

    public StreamDescriptor getDescriptor() {
        return descriptor;
    }
}
