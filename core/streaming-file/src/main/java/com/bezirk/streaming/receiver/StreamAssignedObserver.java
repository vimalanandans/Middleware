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

import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.sender.FileStreamSenderThread;

/**
 * StreamAssignedObserver is an Observer waiting for Assigned status stream records from {@link FileStreamRequestObserver}.
 * Update will be called when the subject StreamBook will be updated with a new entry.
 *
 */

class StreamAssignedObserver extends FileStreamObserver{
    private static final Logger logger = LoggerFactory.getLogger(StreamAssignedObserver.class);
    private static final int THREAD_SIZE = 10;

    private final ExecutorService fileStreamSenderExecutor;
    private final StreamBook streamBook;
    private final ZirkMessageHandler zirkMessageHandler;
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
                if(future.get()){
                    streamBook.updateRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.COMPLETED, -1, null);
                }else{
                    streamBook.updateRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.ERROR, -1, null);
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
            replyToSender(streamRecord, comms);
        }
    }

}
