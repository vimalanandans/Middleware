package com.bezirk.streaming;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

import java.io.File;

/**
 * Created by PIK6KOR on 11/10/2016.
 */

public class StreamRequest extends UnicastControlMessage {

    /**
     * Discriminator that uniquely defines the Control message!
     */
    private final static Discriminator discriminator = Discriminator.STREAM_REQUEST;

    /**
     * Name of the file that needs to be pushed on the recipient
     */
    private String fileName = null;

    /**
     * Sphere to which we are sending this stream to.
     */
    private String sphereId = null;


    public StreamRequest(BezirkZirkEndPoint sender, BezirkZirkEndPoint receiver, String sphereId , File file){
        super(sender, receiver, sphereId, discriminator, true );
        this.fileName = file.getName();
    }

}
