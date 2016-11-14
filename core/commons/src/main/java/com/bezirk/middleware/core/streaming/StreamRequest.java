package com.bezirk.middleware.core.streaming;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

import java.io.File;

/**
 * Created by PIK6KOR on 11/10/2016.
 */

public abstract class StreamRequest extends UnicastControlMessage {

    /**
     * Discriminator that uniquely defines the Control message!
     */
    private final static Discriminator discriminator = Discriminator.STREAM_REQUEST;

    /**
     * default constructor
     * @param sender
     * @param receiver
     * @param sphereId
     */
    public StreamRequest(BezirkZirkEndPoint sender, BezirkZirkEndPoint receiver, String sphereId){
        super(sender, receiver, sphereId, discriminator, true );
    }

}
