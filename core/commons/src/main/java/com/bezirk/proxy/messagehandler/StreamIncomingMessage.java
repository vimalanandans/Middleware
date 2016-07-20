package com.bezirk.proxy.messagehandler;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;


/**
 * Sub class of StreamMessageStatus that is used to give the stream status notification to the
 * ProxyForBezirk.
 */
public final class StreamIncomingMessage extends ServiceIncomingMessage {
    public String streamTopic;
    public String serializedStream;
    /**
     * Path to downloaded file.
     */
    public File file;
    public short localStreamId;
    public BezirkZirkEndPoint sender;

    public StreamIncomingMessage() {
        callbackDiscriminator = "STREAM_UNICAST";
    }

    public StreamIncomingMessage(ZirkId recipientId, String streamTopic, String serializedStream,
                                 File file, short localStreamId, BezirkZirkEndPoint sender) {
        super();
        callbackDiscriminator = "STREAM_UNICAST";
        recipient = recipientId;
        this.streamTopic = streamTopic;
        this.serializedStream = serializedStream;
        this.file = file;
        this.localStreamId = localStreamId;
        this.sender = sender;
    }
}