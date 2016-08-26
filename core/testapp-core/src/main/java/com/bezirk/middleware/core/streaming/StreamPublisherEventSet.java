package com.bezirk.middleware.core.streaming;

import com.bezirk.middleware.messages.EventSet;

/**
 * Created by pik6kor on 8/8/2016.
 */

public class StreamPublisherEventSet extends EventSet {

    public StreamPublisherEventSet() {
        super(StreamPublishEvent.class);
    }

}
