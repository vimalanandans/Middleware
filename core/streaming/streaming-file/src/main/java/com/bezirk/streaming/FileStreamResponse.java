package com.bezirk.streaming;

import com.bezirk.middleware.core.streaming.StreamResponse;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

/**
 * Created by PIK6KOR on 11/14/2016.
 */

public class FileStreamResponse extends StreamResponse {
    /**
     * Name of the file that needs to be pushed on the recipient
     */
    private StreamRecord streamRecord = null;

    public FileStreamResponse(BezirkZirkEndPoint sender, String sphereId, StreamRecord streamRecord){
        super(sender, streamRecord.getRecipientSEP(), sphereId);
        this.streamRecord = streamRecord;
    }

    public StreamRecord getStreamRecord() {
        return streamRecord;
    }

}
