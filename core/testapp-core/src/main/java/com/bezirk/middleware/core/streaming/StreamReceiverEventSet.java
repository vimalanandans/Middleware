package com.bezirk.middleware.core.streaming;

import com.bezirk.middleware.messages.EventSet;

public class StreamReceiverEventSet extends EventSet {

    public StreamReceiverEventSet(){
        super(StreamReceiveEvent.class);
    }
}
