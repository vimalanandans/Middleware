package com.bezirk.streaming.receiver;

import com.bezirk.middleware.core.streaming.StreamRequest;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;
import com.bezirk.streaming.sender.FileStreamSenderThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by PIK6KOR on 11/22/2016.
 */

public class StreamAssignedObserver implements StreamEventObserver {

    //executor which handles the file stream receiving thread.
    private ExecutorService fileStreamSenderExecutor;

    //Thread size
    private static final int THREAD_SIZE = 10;

    StreamAssignedObserver(){
        this.fileStreamSenderExecutor = Executors.newFixedThreadPool(THREAD_SIZE);
    }

    @Override
    public void update(StreamRequest streamRequest, StreamBook streamBook, FileStreamPortFactory portFactory) {

        FileStreamRequest fileStreamRequest = (FileStreamRequest) streamRequest;

        //update the status to addressed.
        StreamRecord streamRecord = fileStreamRequest.getStreamRecord();

        if(StreamRecord.StreamRecordStatus.ASSIGNED == streamRecord.getStreamRecordStatus()){
            // This will be reply to the sender, Start the sender thread and initiate file transmission.
            FileStreamSenderThread fileStreamSenderThread = new FileStreamSenderThread(streamRecord);
            fileStreamSenderExecutor.execute(fileStreamSenderThread);

        }


    }
}
