package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.ZirkId;


/**
 * Sub class of BezirkCallbackMessage that gives back the StreamStatusCallback to proxyForBezirk
 */
public final class StreamStatusMessage extends ServiceIncomingMessage {
    /**
     * StreamStatus id. 0 -&gt; UnSuccessful | 1 -&gt; Successful.
     */
    public int streamStatus = 0;

    /**
     * Id of the pushed Stream
     */
    public short streamId;

    public StreamStatusMessage() {
        callbackDiscriminator = "STREAM_STATUS";
    }

    public StreamStatusMessage(ZirkId recipientId, int streamStatus, short streamId) {
        callbackDiscriminator = "STREAM_STATUS";
        recipient = recipientId;
        this.streamStatus = streamStatus;
        this.streamId = streamId;
    }


}
