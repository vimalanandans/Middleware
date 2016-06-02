package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;


/**
 * Sub class of StreamMessageStatus that is used to give the stream status notification to the
 * ProxyForBezirk.
 */
public final class StreamIncomingMessage extends ServiceIncomingMessage {
    /**
     * Stream Topic
     */
    public String streamTopic;
    /**
     * Serialized Stream request.
     */
    public String serializedStream;
    /**
     * Path to downloaded file.
     */
    public File file;
    /**
     * LocalStreamId
     */
    public short localStreamId;
    /**
     * ZirkEndPoint of the recipient
     */
    public BezirkZirkEndPoint senderSEP;

    public StreamIncomingMessage() {
        callbackDiscriminator = "STREAM_UNICAST";
    }

    public StreamIncomingMessage(ZirkId recipientId, String streamTopic, String serializedStream,
                                 File file, short localStreamId, BezirkZirkEndPoint senderSEP) {
        super();
        callbackDiscriminator = "STREAM_UNICAST";
        recipient = recipientId;
        this.streamTopic = streamTopic;
        this.serializedStream = serializedStream;
        this.file = file;
        this.localStreamId = localStreamId;
        this.senderSEP = senderSEP;
    }
}
