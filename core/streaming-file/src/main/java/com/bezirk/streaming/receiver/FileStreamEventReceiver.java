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
import com.bezirk.middleware.core.comms.CtrlMsgReceiver;
import com.bezirk.middleware.core.comms.processor.EventMsgReceiver;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.streaming.FileStreamRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bezirk.middleware.core.control.messages.ControlMessage.Discriminator.STREAM_REQUEST;

/**
 * FileStreamEventReceiver is a implementation of {@link CtrlMsgReceiver}.
 * This will receive all the control messages related to Streaming. This module will be initialized when
 * Streaming module will be initialized at {@link com.bezirk.streaming.sender.FileStreaming}
 */

public class FileStreamEventReceiver implements CtrlMsgReceiver {

    private final FileStreamRequestObserver fileStreamRequestObserver;

    //logger instance
    private static final Logger logger = LoggerFactory
            .getLogger(FileStreamEventReceiver.class);

    public FileStreamEventReceiver() {
        fileStreamRequestObserver = new FileStreamRequestObserver();
    }

    /**
     * All the received Stream_Request control messages will be passed to stream observer
     * @param id control message ID.
     * @param serializedMsg incoming serialized event message.
     * @return return boolean, if the processing of message was successful or failure.
     */
    @Override
    public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
        if(id.equals(STREAM_REQUEST)){
            if(serializedMsg != null){
                FileStreamRequest fileStreamRequest =  ControlMessage.deserialize(serializedMsg, FileStreamRequest.class);
                fileStreamRequestObserver.incomingStreamRequest(fileStreamRequest);
            }
        }else{
            logger.error("Unknown request type received at Process Control Message typr is {}", id);
        }

        return true;
    }

    /**
     * pass the comms object to StreamAliveObserver as it is required to send reply
     * @param comms instance of COMMS
     */
    public void initFileStreamEventObserver(Comms comms, EventMsgReceiver msgReceiver){
        fileStreamRequestObserver.initStreamRequestObserver(comms, msgReceiver);
    }
}
