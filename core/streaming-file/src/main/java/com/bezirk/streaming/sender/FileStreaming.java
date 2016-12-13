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
package com.bezirk.streaming.sender;

import com.bezirk.middleware.core.actions.StreamAction;
import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.comms.processor.EventMsgReceiver;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.GenerateMsgId;
import com.bezirk.middleware.core.control.messages.UnicastHeader;
import com.bezirk.middleware.core.streaming.Streaming;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.streaming.FileStream;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.receiver.FileStreamEventReceiver;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of {@link Streaming} interface. This is an implementation of File streaming concept.
 * All the request to be streamed will be added to {@link StreamBook}. after adding to the {@link StreamBook#streamingQueue}
 * the a {@link FileStreamRequest} will be sent to the receiver.
 *
 */

public final class FileStreaming implements Streaming {

    //inject the object of comms.
    private Comms comms;
    private final Gson gson = new Gson();
    private StreamBook streamBook = null;
    private static final Logger logger = LoggerFactory.getLogger(FileStreaming.class);
    private static final String id = UUID.randomUUID().toString();

    public FileStreaming(Comms comms, EventMsgReceiver msgReceiver){
        this.comms = comms;
        streamBook = new StreamBook();
        logger.info("FileStreaming module was Initialized!!");

        //register the receiver module
        FileStreamEventReceiver fileStreamEventReceiver = new FileStreamEventReceiver();
        fileStreamEventReceiver.initFileStreamEventObserver(comms, msgReceiver);

        //register the control receiver
        comms.registerControlMessageReceiver(ControlMessage.Discriminator.STREAM_REQUEST,
                fileStreamEventReceiver);

    }

    @Override
    public boolean interruptStream(String streamKey) {
        logger.info("intrrupt streaming was called for key :"+streamKey);
        return false;
    }

    @Override
    public boolean addStreamRecordToQueue(StreamAction streamAction) {
        //prepare stream record from streamAction and save this in the map.

        FileStream fileStream = (FileStream) streamAction.getStreamRequest();
        logger.info("Adding stream record to queue for {},",fileStream.getFile().getName());

        final BezirkZirkEndPoint sender;

        if (comms != null) {
            sender = new BezirkZirkEndPoint(comms.getNodeId(), streamAction.getZirkId());
        } else {
            sender = new BezirkZirkEndPoint(id, streamAction.getZirkId());
        }

        StreamRecord streamRecord = new StreamRecord(streamAction.getStreamId(), (BezirkZirkEndPoint) fileStream.getRecipientEndPoint(), fileStream.getFile(), sender);
        streamRecord.setZirkId(streamAction.getZirkId());

        //add the record to streaming
        boolean isAdded = addStreamRecordToBook(streamRecord);

        //send a event to receiver when the streamRecord is stored
        if(isAdded){
            logger.info("StreamRecord with streamId {} was added to the StreamBook, sending ControlMessage over comms to receiver", streamAction.getStreamId());
            final ControlLedger controlLedger = new ControlLedger();
            final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(sender));
            //sphere will be DEFAULT as of now
            controlLedger.setSphereId("DEFAULT");

            FileStreamRequest streamRequest = new FileStreamRequest((BezirkZirkEndPoint) fileStream.getRecipientEndPoint(), "DEFAULT", streamRecord);
            controlLedger.setMessage(streamRequest);
            controlLedger.setSerializedMessage(gson.toJson(streamRequest));

            final UnicastHeader uHeader = new UnicastHeader();
            uHeader.setRecipient((BezirkZirkEndPoint) fileStream.getRecipientEndPoint());
            uHeader.setSender(sender);
            uHeader.setUniqueMsgId(uniqueMsgId.toString());
            controlLedger.setHeader(uHeader);

            logger.info("sending control ledger from Stream module for request {}", streamRequest.getUniqueKey());
            comms.sendControlLedger(controlLedger);

        }else{
            logger.error("Unable to add StreamRecord to stream queue !!!!!");
        }
        return false;
    }

    /**
     * add Stream Record to Stream Sending queue.
     * @param sRecord streamRecord to be added to the stream queue
     * @return boolean
     */
    private boolean addStreamRecordToBook(StreamRecord sRecord) {
        synchronized (this) {
            if (!streamBook.hasStreamRecord(sRecord.getStreamId())) {
                streamBook.addStreamingRecordToBook(sRecord);
                logger.debug("addStreamRecordToBook was successful for  {},",sRecord.getFile().getName());
                return true;
            } else {
                logger.debug("addStreamRecordToBook has failed for  {},",sRecord.getFile().getName());
                return false;
            }
        }
    }
}
