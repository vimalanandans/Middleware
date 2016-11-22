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

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.util.ValidatorUtility;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by PIK6KOR on 11/11/2016.
 */

public class StreamQueueProcessor implements Runnable {
    @Override
    public void run() {

    }

    /*public StreamQueueProcessor(){

    }

    @Override
    public void run() {
        boolean running = true;
        boolean bezirkCallbackPresent = false;
        while (running) {
            if (Thread.currentThread().isInterrupted()) {
                running = false;
                continue;
            }

            //using CopyOnWriteArrayList for sync write and reads.
            List<ControlMessage> streamQueue = new CopyOnWriteArrayList<>(
                    msgQueue.getQueue()); // pop the StreamDescriptor record
            Iterator<ControlMessage> it = streamQueue.iterator();

            while (it.hasNext()) {
                StreamRecord streamRecord = (StreamRecord) it.next();

                if (StreamRecordStatus.LOCAL == streamRecord.getStreamRecordStatus()) {
                    processLocalStreamMessage(bezirkCallbackPresent, streamRecord);

                } else if (StreamRecordStatus.ADDRESSED == streamRecord.getStreamRecordStatus()) {
                    logger.debug("StreamDescriptor Request is already Addressed.");
                } else if (streamRecord.getStreamRecordStatus()== StreamRecordStatus.READY) {
                    processStreamReadyMessage(streamRecord);
                } else if (streamRecord.getStreamRecordStatus() == StreamRecordStatus.BUSY) {
                    processStreamBusyMessage(bezirkCallbackPresent, streamRecord);
                }
                msgQueue.removeFromQueue(streamRecord);

                //punith.. remove from active stream map
            }
        }
    }*/
}
