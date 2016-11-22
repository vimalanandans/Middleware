package com.bezirk.streaming;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by PIK6KOR on 11/14/2016.
 */

public class StreamBook {

    //Streaming Request queue.
    private final Map<Short, StreamRecord> streamingQueue = new ConcurrentHashMap<Short, StreamRecord>();

    //add to streamBook
    public void addStreamingRecordToBook(StreamRecord streamRecord){
        streamingQueue.put(streamRecord.getStreamId(), streamRecord);
    }


    //update the streamBook
    public void updateStreamRecordInBook(Short streamKey, StreamRecord.StreamRecordStatus updateStatus, Integer port){
        //get the stream based on the id and update the status
        StreamRecord streamRecord   = (StreamRecord) streamingQueue.get(streamKey);
        streamRecord.setStreamRecordStatus(updateStatus);

        if(port != null){
            streamRecord.setRecipientPort(port);
        }

        streamingQueue.put(streamKey, streamRecord);

    }


    public boolean hasStreamRecord(Short streamId) {
        return streamingQueue.containsKey(streamId);
    }
}
