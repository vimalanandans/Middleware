package com.bezirk.middleware.core.streaming;

import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

/**
 * Created by PIK6KOR on 11/10/2016.
 */

public abstract class StreamResponse extends UnicastControlMessage {
    /**
     * Discriminator that uniquely defines the Control message!
     */
    private final static Discriminator discriminator = Discriminator.STREAM_RESPONSE;


    public StreamResponse(BezirkZirkEndPoint sender, BezirkZirkEndPoint receiver, String sphereId){
        super(sender, receiver, sphereId, discriminator, true );
    }
}
