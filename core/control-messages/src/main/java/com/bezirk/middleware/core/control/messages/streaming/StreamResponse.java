/**
 * @author: Vijet Badignannvar (bvijet@in.bosch.com)
 */
package com.bezirk.middleware.core.control.messages.streaming;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.core.streaming.control.Objects.StreamRecord;

/**
 * This Message is internally sent by the Bezirk for hand shaking with the sender.
 */
public class StreamResponse extends UnicastControlMessage {
    /**
     * Discriminator that uniquely defines the Control message!
     */
    private final static Discriminator discriminator = ControlMessage.Discriminator.STREAM_RESPONSE;

    /**
     * Status of the Recipient. PENDING or READY or ADDRESSED or BUSY
     */
    public StreamRecord.StreamRecordStatus status;

    /**
     * The ip at which the recipient is listening. zirk end point is generic. may or maynot contain ip address
     */
    public String streamIp = "";

    /**
     * The port at which the recipient is listening
     */
    public int streamPort = -1;

    /**
     * This is StreamRequestKey that has to be set by taking the key from STREAM_REQUEST
     */
    public String sReqKey;

    public StreamResponse(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, String sphereName, String strmKey,
                          StreamRecord.StreamRecordStatus status, String streamIp, int streamPort) {
        super(sender, recipient, sphereName, discriminator, false, strmKey);
        this.status = status;
        this.streamIp = streamIp;
        this.streamPort = streamPort;

    }
}
