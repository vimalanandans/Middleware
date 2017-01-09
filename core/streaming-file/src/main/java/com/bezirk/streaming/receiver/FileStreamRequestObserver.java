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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.StreamAction;
import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.comms.processor.EventMsgReceiver;
import com.bezirk.middleware.core.streaming.StreamReceiver;
import com.bezirk.middleware.core.streaming.StreamRequest;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;

/**
 * Implementaion of {@link StreamReceiver} and {@link ZirkMessageHandler}
 * all the Stream receiving events will be received here and passed to observers, There are 2 observers as now,
 * {@link StreamAliveObserver} and {@link StreamAssignedObserver}.
 */

class FileStreamRequestObserver extends Observable implements StreamReceiver, ZirkMessageHandler{
    private static final Logger logger = LoggerFactory.getLogger(FileStreamRequestObserver.class);
    private final EventMsgReceiver eventMsgReceiver;

    FileStreamRequestObserver(@NotNull Comms comms, @NotNull EventMsgReceiver eventMsgReceiver){
        this.eventMsgReceiver = eventMsgReceiver;
        final StreamBook streamBook = new StreamBook();
        final FileStreamPortFactory portFactory = new FileStreamPortFactory();

        //initialize the observers
        addObserver(new StreamAliveObserver(comms, streamBook, portFactory, this));
        addObserver(new StreamAssignedObserver(comms, streamBook, this));

        logger.trace("Initialized the Streaming observers");
    }

    @Override
    public void callBackToZirk(StreamRecord streamRecord){
        final StreamAction streamAction  = new StreamAction(streamRecord.getZirkId());
        streamAction.setStreamId(streamRecord.getStreamId());
        streamAction.setStreamStatus(streamRecord.getStreamRecordStatus().toString());
        streamAction.setBezirkAction(BezirkAction.ACTION_ZIRK_RECEIVE_STREAM);

        eventMsgReceiver.processStreamEvent(streamAction);
    }

    @Override
    public void incomingStreamRequest(StreamRequest streamRequest) {
        if(streamRequest != null){
            final FileStreamRequest fileStreamRequest = (FileStreamRequest) streamRequest;
            if(fileStreamRequest.getStreamRecord()!=null && fileStreamRequest.getStreamRecord().getFile()!=null) {
                logger.debug("received a new stream request for file {}", fileStreamRequest.getStreamRecord().getFile().getName());
                setChanged();
                notifyObservers(fileStreamRequest);
            }else{
                logger.error("File Stream Request has insufficient data to be processed by Observers!");
            }

        }else{
            logger.error("Incoming Stream Request was null!");
        }

    }

}

interface ZirkMessageHandler{
    void callBackToZirk(StreamRecord streamRecord);
}


