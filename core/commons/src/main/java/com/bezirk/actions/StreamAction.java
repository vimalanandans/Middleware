package com.bezirk.actions;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.api.impl.ZirkId;

public abstract class StreamAction extends ZirkAction {
    private final ZirkEndPoint recipient;
    private final StreamDescriptor descriptor;
    private final short streamId;

    public StreamAction(ZirkId zirkId, ZirkEndPoint recipient, StreamDescriptor descriptor, short streamId) {
        super(zirkId);

        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send a streamDescriptor to a null recipient");
        }

        if (descriptor == null) {
            throw new IllegalArgumentException("Null or empty streamDescriptor specified when sending " +
                    "a file");
        }

        if(streamId < 0 ){
            throw new IllegalArgumentException("Invalid streamId specified streamId is" +streamId
                    +" stream id is Invalid" );
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
