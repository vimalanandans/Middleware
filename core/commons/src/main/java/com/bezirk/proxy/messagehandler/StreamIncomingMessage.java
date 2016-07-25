package com.bezirk.proxy.messagehandler;

import com.bezirk.actions.BezirkAction;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;


/**
 * Sub class of StreamMessageStatus that is used to give the stream status notification to the
 * ProxyForBezirk.
 */
public final class StreamIncomingMessage extends ServiceIncomingMessage {
    private final String streamTopic;
    private final String serializedStream;
    /**
     * Path to downloaded file.
     */
    private final File file;
    private final short localStreamId;
    private final BezirkZirkEndPoint sender;

    public StreamIncomingMessage(ZirkId recipientId, String streamTopic, String serializedStream,
                                 File file, short localStreamId, BezirkZirkEndPoint sender) {
        super(BezirkAction.ACTION_ZIRK_RECEIVE_STREAM, recipientId);

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
}
