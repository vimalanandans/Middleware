/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.streaming.receiver;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.sender.FileStreamSenderThread;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * StreamAssignedObserver is an Observer waiting for Assigned status stream records from {@link FileStreamRequestObserver}.
 * Update will be called when the subject StreamBook will be updated with a new entry.
 *
 */

class StreamAssignedObserver implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(StreamAssignedObserver.class);
    private static final int THREAD_SIZE = 10;

    private final ExecutorService fileStreamSenderExecutor;
    private final StreamBook streamBook;
    private final ZirkMessageHandler zirkMessageHandler;
    private final Gson gson = new Gson();
    private final Comms comms;

    StreamAssignedObserver(Comms comms, StreamBook streamBook, ZirkMessageHandler zirkMessageHandler){
        this.comms = comms;
        this.streamBook = streamBook;
        this.fileStreamSenderExecutor = Executors.newFixedThreadPool(THREAD_SIZE);
        this.zirkMessageHandler = zirkMessageHandler;
    }

    @Override
    public void update(Observable observable, Object streamRequest) {

        final FileStreamRequest fileStreamRequest = (FileStreamRequest) streamRequest;
        final StreamRecord streamRecord = fileStreamRequest.getStreamRecord();

        if(StreamRecord.StreamRecordStatus.ASSIGNED == streamRecord.getStreamRecordStatus()){
            //Start the sender thread and initiate file transmission.
            final FileStreamSenderThread fileStreamSenderThread = new FileStreamSenderThread(streamRecord);
            final Future<Boolean> future = fileStreamSenderExecutor.submit(fileStreamSenderThread);
            try {
                while (!future.isDone()) {
                    //sleep current thread for 100ms before checking if the future task was completed.
                    Thread.sleep(100);
                }
                if(future.get()){
                    streamRecord.setStreamRecordStatus(StreamRecord.StreamRecordStatus.COMPLETED);
                    streamBook.updateStreamRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.COMPLETED, null, null);
                }else{
                    streamRecord.setStreamRecordStatus(StreamRecord.StreamRecordStatus.ERROR);
                    streamBook.updateStreamRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.BUSY, null, null);
                }
            } catch (InterruptedException e) {
                logger.error("InterruptedException has occurred during File stream SENDING!", e);
                Thread.currentThread().interrupt();
            } catch (Exception e){
                logger.error("Exception has occurred during File stream SENDING!", e);
            }

            logger.debug("stream sending was sucessfull for streamID {} giving a callback to zirk!", streamRecord.getStreamId());
            //give a callback status to zirk of updated status and reply to sender with updated status.
            zirkMessageHandler.callBackToZirk(streamRecord);
            replyToSender(streamRecord);
        }
    }

    /**
     * After updating the #StreamBook, Send a control message to sender with updated #StreamRecord status.
     * @param streamRecord streamRecord.
     */
    private void replyToSender(StreamRecord streamRecord) {
        final ControlLedger controlLedger = new ControlLedger();
        controlLedger.setSphereId("DEFAULT");

        final FileStreamRequest streamResponse = new FileStreamRequest(streamRecord.getSenderServiceEndPoint(), "DEFAULT", streamRecord);
        controlLedger.setMessage(streamResponse);
        controlLedger.setSerializedMessage(gson.toJson(streamResponse));

        logger.debug("sending a reply to sender for stream request {}", streamRecord.getStreamId());
        comms.sendControlLedger(controlLedger);
    }
}
