package com.bezirk.actions;

import com.bezirk.actions.BezirkAction;
import com.bezirk.actions.ZirkAction;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;

public final class ReceiveFileStreamAction extends ZirkAction {
    private final String streamTopic;
    private final String serializedStream;
    /**
     * Path to downloaded file.
     */
    private final File file;
    private final short localStreamId;
    private final BezirkZirkEndPoint sender;

    public ReceiveFileStreamAction(ZirkId recipientId, String streamTopic, String serializedStream,
                                   File file, short localStreamId, BezirkZirkEndPoint sender) {
        super(recipientId);

        this.streamTopic = streamTopic;
        this.serializedStream = serializedStream;
        this.file = file;
        this.localStreamId = localStreamId;
        this.sender = sender;
    }

    public String getStreamTopic() {
        return streamTopic;
    }

    public String getSerializedStream() {
        return serializedStream;
    }

    public File getFile() {
        return file;
    }

    public short getLocalStreamId() {
        return localStreamId;
    }

    public BezirkZirkEndPoint getSender() {
        return sender;
    }

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_ZIRK_RECEIVE_STREAM;
    }
}
