package com.bezirk.middleware.core.streaming;

import com.bezirk.middleware.messages.EventSet;

public class StreamPublisherEventSet extends EventSet {

    public StreamPublisherEventSet() {
        super(StreamPublishEvent.class);
    }

}
