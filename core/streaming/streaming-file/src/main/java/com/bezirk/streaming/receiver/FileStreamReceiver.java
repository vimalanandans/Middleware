package com.bezirk.streaming.receiver;

import com.bezirk.middleware.core.streaming.StreamReceiver;
import com.bezirk.middleware.core.streaming.StreamRequest;
import com.bezirk.middleware.streaming.FileStream;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;

/**
 * Created by PIK6KOR on 11/14/2016.
 */

public class FileStreamReceiver implements StreamReceiver{

    private StreamBook streamBook  = null;

    public FileStreamReceiver(){
        streamBook = new StreamBook();
    }

    private void initFileStreamReceiver(){
        streamBook.initStreamBook();
    }

    @Override
    public void incomingStreamRequest(StreamRequest streamRequest) {
        FileStreamRequest fileStreamRequest = (FileStreamRequest) streamRequest;

        //update the status to addressed.
        StreamRecord streamRecord = fileStreamRequest.getStreamRecord();
        if(StreamRecord.StreamRecordStatus.ALIVE == streamRecord.getStreamRecordStatus()){
            streamRecord.setStreamRecordStatus(StreamRecord.StreamRecordStatus.ADDRESSED);

            //add the stream record to stream book.
            streamBook.addStreamingRecordToBook(streamRecord);

        }else {
            //unknown for now
        }


        //assign a port to stream record from the port factory.


        //update the status to assigned.


    }
}
