package com.bezirk.streaming;

import com.bezirk.middleware.core.streaming.StreamRequest;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

import java.io.File;

/**
 * Created by PIK6KOR on 11/11/2016.
 */

public class FileStreamRequest extends StreamRequest {

    /**
     * Name of the file that needs to be pushed on the recipient
     */
    private StreamRecord streamRecord = null;

    public FileStreamRequest(BezirkZirkEndPoint sender, String sphereId, StreamRecord streamRecord){
        super(sender, streamRecord.getRecipientSEP(), sphereId);
        this.streamRecord = streamRecord;
    }

    public StreamRecord getStreamRecord() {
        return streamRecord;
    }
}
