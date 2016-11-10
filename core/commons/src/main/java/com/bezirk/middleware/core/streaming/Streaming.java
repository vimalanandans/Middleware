package com.bezirk.middleware.core.streaming;

import com.bezirk.middleware.core.actions.StreamAction;
import com.bezirk.streaming.StreamRecord;

/**
 * Created by PIK6KOR on 11/3/2016.
 */

public interface Streaming {

    /**
     * Interrupt a single streaming thread it could be either receiving thread or sender thread.
     */
    boolean interruptStream(final String streamKey);

    /**
     * Adds a Stream Record to stream book.
     */
    boolean addStreamRecordToQueue(StreamAction streamRecord);

}
