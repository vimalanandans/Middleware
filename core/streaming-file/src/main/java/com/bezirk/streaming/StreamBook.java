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
package com.bezirk.streaming;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StreamBook will be a ledger for all the incoming and outgoing stream request.
 * It is managed by a {@link ConcurrentHashMap}. All the latest request with updated stream status
 * will be available in this object.
 */
public class StreamBook {

    //Streaming Request queue.
    private final Map<String, StreamRecord> streamingQueue = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(StreamBook.class);


    public void addRecordinBook(StreamRecord streamRecord){
        streamingQueue.put(streamRecord.getStreamId(), streamRecord);
        logger.debug("added a new stream record {} to stream book", streamRecord.getFile().getName());
    }


    public void updateRecordInBook(String streamKey, StreamRecord.StreamRecordStatus updateStatus, int port, String deviceIp){
        //get the stream based on the id and update the status
        final StreamRecord streamRecord = streamingQueue.get(streamKey);
        streamRecord.setStreamRecordStatus(updateStatus);

        if(port > 0){
            streamRecord.setRecipientPort(port);
            logger.debug("updated stream record with Port {} to stream book", port);
        }

        if(deviceIp != null){
            streamRecord.setRecipientIp(deviceIp);
            logger.debug("updated stream record  with device IP {} to stream book", deviceIp);
        }
        streamingQueue.put(streamKey, streamRecord);

    }

    public boolean hasRecord(String streamId) {
        return streamingQueue.containsKey(streamId);
    }

    public StreamRecord getRecordStatus(String streamId){
        return streamingQueue.get(streamId);
    }
}
