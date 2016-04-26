package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

import java.io.File;


/**
 * Sub class of StreamMessageStatus that is used to give the stream status notification to the ProxyForUhu.
 */
public final class StreamIncomingMessage extends ServiceIncomingMessage {
    /**
     * Stream Topic
     */
    public String streamTopic;
    /**
     * Serialized Stream request.
     */
    public String serialzedStream;
    /**
     * Path to downloaded file.
     */
    public File file;
    /**
     * LocalStreamId
     */
    public short localStreamId;
    /**
     * ServiceEndPoint of the recipient
     */
    public UhuServiceEndPoint senderSEP;

    public StreamIncomingMessage() {
        callbackDiscriminator = "STREAM_UNICAST";
    }

    public StreamIncomingMessage(UhuServiceId recipientId, String streamTopic, String serialzedStream,
                                 File file, short localStreamId, UhuServiceEndPoint senderSEP) {
        super();
        callbackDiscriminator = "STREAM_UNICAST";
        recipient = recipientId;
        this.streamTopic = streamTopic;
        this.serialzedStream = serialzedStream;
        this.file = file;
        this.localStreamId = localStreamId;
        this.senderSEP = senderSEP;
    }
}
