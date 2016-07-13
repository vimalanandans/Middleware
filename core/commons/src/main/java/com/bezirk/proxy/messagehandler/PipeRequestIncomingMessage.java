package com.bezirk.proxy.messagehandler;

import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.pipe.policy.ext.BezirkPipePolicy;
import com.bezirk.proxy.api.impl.ZirkId;

public class PipeRequestIncomingMessage extends ServiceIncomingMessage {

    private Pipe pipe;
    private String pipeReqId;
    private BezirkPipePolicy allowedIn;
    private BezirkPipePolicy allowedOut;

    public PipeRequestIncomingMessage() { // Empty Constructor for Gson
        this.callbackDiscriminator = "PIPE-APPROVED";
    }

    public PipeRequestIncomingMessage(Pipe pipe, String pipeReqId, BezirkPipePolicy allowedIn, BezirkPipePolicy allowedOut, ZirkId recipient) {
        this.callbackDiscriminator = "PIPE-APPROVED";
        this.pipe = pipe;
        this.pipeReqId = pipeReqId;
        this.allowedIn = allowedIn;
        this.allowedOut = allowedOut;
        this.recipient = recipient;
    }


    public Pipe getPipe() {
        return pipe;
    }

    public String getPipeReqId() {
        return pipeReqId;
    }

    public BezirkPipePolicy getAllowedIn() {
        return allowedIn;
    }

    public BezirkPipePolicy getAllowedOut() {
        return allowedOut;
    }
}
