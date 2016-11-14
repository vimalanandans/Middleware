package com.bezirk.streaming.receiver;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.streaming.StreamReceiver;
import com.bezirk.middleware.core.streaming.StreamRequest;
import com.bezirk.middleware.streaming.FileStream;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.FileStreamResponse;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;
import com.google.gson.Gson;

/**
 * Created by PIK6KOR on 11/14/2016.
 */

public class FileStreamReceiver implements StreamReceiver{

    private StreamBook streamBook  = null;

    private FileStreamPortFactory portFactory = null;

    //this has to be dependency injected.
    private Comms comms = null;

    Gson gson = new Gson();

    public FileStreamReceiver(Comms comms){
        streamBook = new StreamBook();
        portFactory = new FileStreamPortFactory();
        this.comms = comms;
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
        Integer assignedPort = portFactory.getActivePort(streamRecord.getStreamId());

        //update the status to assigned and assign the port to stream record.
        streamRecord.setRecipientPort(assignedPort);
        streamBook.updateStreamRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.ASSIGNED, assignedPort);

        //send a ControlMessage(StreamResponse) back to sender with updated information
        ControlLedger controlLedger = new ControlLedger();

        //sphere will be DEFAULT as of now
        controlLedger.setSphereId("DEFAULT");

        FileStreamResponse streamResonse = new FileStreamResponse(fileStreamRequest.getSender(), "DEFAULT", streamRecord);
        controlLedger.setMessage(streamResonse);
        controlLedger.setSerializedMessage(gson.toJson(streamResonse));

        comms.sendControlLedger(controlLedger);

    }
}
