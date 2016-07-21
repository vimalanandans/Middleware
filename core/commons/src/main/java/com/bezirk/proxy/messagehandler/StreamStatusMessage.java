package com.bezirk.proxy.messagehandler;

import com.bezirk.proxy.api.impl.ZirkId;

/**
 * Sub class of BezirkCallbackMessage that gives back the StreamStatusCallback to proxyForBezirk
 */
public final class StreamStatusMessage extends ServiceIncomingMessage {
    /**
     * StreamStatus id. 0 -&gt; UnSuccessful | 1 -&gt; Successful.
     */
    private final int streamStatus;

    /**
     * Id of the pushed StreamDescriptor
     */
    private final short streamId;

    public StreamStatusMessage(ZirkId recipientId, int streamStatus, short streamId) {
        super("STREAM_STATUS", recipientId);

        this.streamStatus = streamStatus;
        this.streamId = streamId;
    }

    public short getStreamId() {
        return streamId;
    }

    public int getStreamStatus() {
        return streamStatus;
    }
}
