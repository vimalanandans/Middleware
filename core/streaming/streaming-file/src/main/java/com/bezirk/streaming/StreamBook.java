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

    // creates thread pool with one thead
    private ExecutorService streamingQueueExecutor = null;

    public StreamBook(){
        //start  the executor which will loop through the stream book.
        streamingQueueExecutor  = Executors.newSingleThreadExecutor();
    }

    public void initStreamBook(){
        streamingQueueExecutor.execute(new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        //loop through the streaming queue until streaming is running
                        while (true) {

                            Iterator<Map.Entry<Short, StreamRecord>> it = streamingQueue.entrySet().iterator();

                            while (it.hasNext()) {
                                StreamRecord streamRecord = (StreamRecord) it.next();

                                if (StreamRecord.StreamRecordStatus.ALIVE == streamRecord.getStreamRecordStatus()) {
                                    processAliveRecords(streamRecord);

                                } else if (StreamRecord.StreamRecordStatus.ADDRESSED == streamRecord.getStreamRecordStatus()) {
                                    processAddressedRecords(streamRecord);
                                } else {
                                    //either dead(completed) or (//processing  || //
                                }


                            }

                        }
                    }
                }
        ));
    }

    private void processAliveRecords(StreamRecord streamRecord){

    }

    private void processAddressedRecords(StreamRecord streamRecord){

    }

    public ExecutorService getStreamingQueueExecutor() {
        return streamingQueueExecutor;
    }

    //add to streamBook
    public void addStreamingRecordToBook(StreamRecord streamRecord){
        streamingQueue.put(streamRecord.getStreamId(), streamRecord);
    }


    //update the streamBook
    public void updateStreamRecordInBook(Short streamKey, StreamRecord.StreamRecordStatus updateStatus){
        //get the stream based on the id and update the status
        StreamRecord streamRecord   = (StreamRecord) streamingQueue.get(streamKey);
        streamRecord.setStreamRecordStatus(updateStatus);

        streamingQueue.put(streamKey, streamRecord);

    }


    public boolean hasStreamRecord(Short streamId) {
        return streamingQueue.containsKey(streamId);
    }
}
