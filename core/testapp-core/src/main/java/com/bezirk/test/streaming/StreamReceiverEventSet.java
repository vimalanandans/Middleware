package com.bezirk.test.streaming;

import com.bezirk.middleware.messages.EventSet;

/**
 * Created by pik6kor on 8/8/2016.
 */

public class StreamReceiverEventSet extends EventSet {

    public StreamReceiverEventSet(){
        super(StreamReceiveEvent.class);
    }
}
