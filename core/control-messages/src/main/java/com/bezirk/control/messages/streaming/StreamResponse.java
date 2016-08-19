/**
 * @author: Vijet Badignannvar (bvijet@in.bosch.com)
 */
package com.bezirk.control.messages.streaming;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.streaming.control.Objects.StreamRecord;

/**
 * This Message is internally sent by the Bezirk for hand shaking with the sender.
 */
public class StreamResponse extends com.bezirk.control.messages.UnicastControlMessage {
    /**
     * Discriminator that uniquely defines the Control message!
     */
    private final static Discriminator discriminator = com.bezirk.control.messages.ControlMessage.Discriminator.StreamResponse;

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
     * This is StreamRequestKey that has to be set by taking the key from StreamRequest
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
