package com.bezirk.streaming.receiver;

import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.StreamRecord;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * when we receive a Busy status from receiver
 *
 */

public class StreamBusyObserver implements Observer {


    @Override
    public void update(Observable observable, Object streamRequest) {

        FileStreamRequest fileStreamRequest = (FileStreamRequest) streamRequest;
        StreamRecord streamRecord = fileStreamRequest.getStreamRecord();

        if(StreamRecord.StreamRecordStatus.BUSY == streamRecord.getStreamRecordStatus()){
            //update the map and status record for as Busy.

            //give a callback to Zirk, that the receiver is Busy.
        }
    }
}
