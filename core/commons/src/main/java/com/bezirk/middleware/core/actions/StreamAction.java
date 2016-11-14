package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.streaming.Stream;

/**
 * Created by PIK6KOR on 11/4/2016.
 */

public class StreamAction extends ZirkAction {

    private Short streamId;
    private BezirkAction bezirkAction;
    private Stream stream;

    //TODO : Punith does this require a default constructor??

    public StreamAction(Short streamId, Stream stream, BezirkAction bezirkAction){
        super(stream.getZirkId());
        this.stream  = stream;
        this.streamId = streamId;
        this.bezirkAction = bezirkAction;
    }

    @Override
    public BezirkAction getAction() {
        return bezirkAction;
    }

    public Stream getStreamRequest() {
        return stream;
    }

    public Short getStreamId() {
        return streamId;
    }
}
