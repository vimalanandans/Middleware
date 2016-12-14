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

import java.io.File;

/**
 *
 *
 */

public class StreamEvent {

    //stream status
    private StreamRecordStatus streamRecordStatus;

    //streamfile information
    /*private File file;*/

    //primary key of the stream information
    private Short streamId;

    public StreamEvent(String streamRecordStatus, /*File file, */Short streamId){
        this.streamRecordStatus = StreamRecordStatus.valueOf(streamRecordStatus);
        /*this.file = file;*/
        this.streamId = streamId;
    }

    public Short getStreamId() {
        return streamId;
    }

    public StreamRecordStatus getStreamRecordStatus() {
        return streamRecordStatus;
    }

    /* Streaming Status indicates the status of the Streams.
                     * ALIVE -  Status for sender, when we has initiated a request.
                     * ADDRESSED   -  indicating the recipient has received the request.
                     * ASSIGNED - If the Port was free for recipient, and has agreed to receive the file.
                     * BUSY -  indicating the receipient is busy, All the active ports are consumed.
                     * COMPLETED -  Indicates that file transfer was complete.
                     * */
    public enum StreamRecordStatus {
        ALIVE, ADDRESSED, ASSIGNED , BUSY, COMPLETED, ERROR

    }

}
