package com.bezirk.streaming.receiver;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.streaming.StreamRequest;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.FileStreamResponse;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * StreamAliveObserver will be a EventObserver.. When the StreamStatus will be ALIVE this cde will handle the state.
 */

class StreamAliveObserver implements StreamEventObserver {

    //executor which handles the file stream receiving thread.
    private ExecutorService fileStreamReceiverExecutor;

    //size of thread size
    private static final int THREAD_SIZE = 10;

    //comms injected.
    private Comms comms = null;

    //Gson dependency
    private final Gson gson = new Gson();

    //streamBook and Portfactory also needs to be dependency injected.

    StreamAliveObserver(Comms comms){
        this.comms = comms;
        this.fileStreamReceiverExecutor = Executors.newFixedThreadPool(THREAD_SIZE);
    }

    @Override
    public void update(StreamRequest streamRequest, StreamBook streamBook, FileStreamPortFactory portFactory) {
        FileStreamRequest fileStreamRequest = (FileStreamRequest) streamRequest;

        //update the status to addressed.
        StreamRecord streamRecord = fileStreamRequest.getStreamRecord();

        if(StreamRecord.StreamRecordStatus.ALIVE == streamRecord.getStreamRecordStatus()){
            //when the status is alive
            streamRecord.setStreamRecordStatus(StreamRecord.StreamRecordStatus.ADDRESSED);

            //add the stream record to stream book.
            streamBook.addStreamingRecordToBook(streamRecord);

            //assign a port to stream record from the port factory.
            Integer assignedPort = portFactory.getActivePort(streamRecord.getStreamId());

            if(assignedPort != -1){
                //update the status to assigned and assign the port to stream record.
                streamRecord.setRecipientPort(assignedPort);
                streamBook.updateStreamRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.ASSIGNED, assignedPort);

                //start the receiver thread and send a reply to sender.
                FileStreamReceivingThread streamReceivingThread =new FileStreamReceivingThread(assignedPort, streamRecord.getFile(), portFactory);
                fileStreamReceiverExecutor.submit(new Thread(streamReceivingThread));

                if(/*featureCallbackReturnsTrue*/ true) {

                    //update the status to compete and notify the sender that the file receive is complete.
                    streamBook.updateStreamRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.COMPLETED, assignedPort);

                    //reply to the sender with status
                    replyToSender(fileStreamRequest, streamRecord);
                }
            }else{
                //update the stream record to busy status and send it to .
                streamRecord.setRecipientPort(assignedPort);
                streamBook.updateStreamRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.BUSY, assignedPort);

                //reply to the sender with status
                replyToSender(fileStreamRequest, streamRecord);
            }


        }

    }

    /**
     * reply to sender with the given stream staus.
     * @param fileStreamRequest
     * @param streamRecord
     */
    private void replyToSender(FileStreamRequest fileStreamRequest, StreamRecord streamRecord) {
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
