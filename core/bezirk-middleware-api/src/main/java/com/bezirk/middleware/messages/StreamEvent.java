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
package com.bezirk.middleware.messages;

import com.bezirk.middleware.streaming.Stream;

/**
 * StreamEvent is a callback object given to Zirk when there is  an update to StreamStatus.
 * An instance will be created in <code>ZirkMessageReceiver</code> and is passed to {@link com.bezirk.middleware.streaming.Stream#setEventReceiver(Stream.StreamEventReceiver)}
 */

public class StreamEvent {

    private final StreamRecordStatus streamRecordStatus;
    private final String streamId;
    private final String fileName;

    public StreamEvent(String streamRecordStatus, String streamId, String fileName){
        this.streamRecordStatus = StreamRecordStatus.valueOf(streamRecordStatus);
        this.streamId = streamId;
        this.fileName = fileName;
    }

    public String getStreamId() {
        return streamId;
    }

    public String getFileName() {
        return fileName;
    }

    public StreamRecordStatus getStreamRecordStatus() {
        return streamRecordStatus;
    }

    /** Streaming Status indicates the status of the Streams.
     * ALIVE -  Status for sender, when we has initiated a request.
     * ADDRESSED   -  indicating the recipient has received the request.
     * ASSIGNED - If the Port was free for recipient, and has agreed to receive the file.
     * BUSY -  indicating the receipient is busy, All the active ports are consumed.
     * COMPLETED -  Indicates that file transfer was complete.
     * ERROR - Indicates that an error occured during streaming
     */
    public enum StreamRecordStatus {
        ALIVE, ADDRESSED, ASSIGNED , BUSY, COMPLETED, ERROR

    }

}
