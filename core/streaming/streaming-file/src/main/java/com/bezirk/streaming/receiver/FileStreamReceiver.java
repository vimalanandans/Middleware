package com.bezirk.streaming.receiver;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.streaming.StreamReceiver;
import com.bezirk.middleware.core.streaming.StreamRequest;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.FileStreamResponse;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;
import com.bezirk.streaming.sender.FileStreamSenderThread;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by PIK6KOR on 11/14/2016.
 */

public class FileStreamReceiver implements StreamReceiver{

    private StreamBook streamBook  = null;
    private FileStreamPortFactory portFactory = null;
    //this has to be dependency injected.
    private Comms comms = null;
    Gson gson = new Gson();
    static final int THREAD_SIZE = 10;

    //executor which handles the file stream receiving thread.
    private ExecutorService fileStreamReceiverExecutor;

    //executor which handles the file stream receiving thread.
    private ExecutorService fileStreamSenderExecutor;

    public FileStreamReceiver(Comms comms){
        streamBook = new StreamBook();
        portFactory = new FileStreamPortFactory();
        this.comms = comms;

        this.fileStreamReceiverExecutor = Executors.newFixedThreadPool(THREAD_SIZE);
        this.fileStreamSenderExecutor = Executors.newFixedThreadPool(THREAD_SIZE);
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



        }else if(StreamRecord.StreamRecordStatus.ASSIGNED == streamRecord.getStreamRecordStatus()){
            // This will be reply to the sender, Start the sender thread and initiate file transmission.
            FileStreamSenderThread fileStreamSenderThread = new FileStreamSenderThread(streamRecord);
            fileStreamSenderExecutor.execute(fileStreamSenderThread);

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
