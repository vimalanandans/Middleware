package com.bezirk.actions;

import com.bezirk.actions.BezirkAction;
import com.bezirk.actions.ZirkAction;
import com.bezirk.proxy.api.impl.ZirkId;

public final class StreamStatusAction extends ZirkAction {
    /**
     * StreamStatus id. 0 -&gt; UnSuccessful | 1 -&gt; Successful.
     */
    private final int streamStatus;

    /**
     * Id of the pushed StreamDescriptor
     */
    private final short streamId;

    public StreamStatusAction(ZirkId zirkId, int streamStatus, short streamId) {
        super(zirkId);

        this.streamStatus = streamStatus;
        this.streamId = streamId;
    }

    public short getStreamId() {
        return streamId;
    }

    public int getStreamStatus() {
        return streamStatus;
    }

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_ZIRK_RECEIVE_STREAM_STATUS;
    }
}
