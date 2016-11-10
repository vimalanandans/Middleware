package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.streaming.StreamRequest;

/**
 * Created by PIK6KOR on 11/4/2016.
 */

public class StreamAction extends ZirkAction {

    private Short streamId;
    private BezirkAction bezirkAction;
    private StreamRequest streamRequest;

    //TODO : Punith does this require a default constructor??

    public StreamAction(Short streamId, StreamRequest streamRequest, BezirkAction bezirkAction){
        super(streamRequest.getZirkId());
        this.streamRequest  = streamRequest;
        this.streamId = streamId;
        this.bezirkAction = bezirkAction;
    }

    @Override
    public BezirkAction getAction() {
        return bezirkAction;
    }

    public StreamRequest getStreamRequest() {
        return streamRequest;
    }

    public Short getStreamId() {
        return streamId;
    }
}
