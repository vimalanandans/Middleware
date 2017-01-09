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

import java.io.File;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

/**
 * StreamRecord class maintains metadata for a {@link com.bezirk.middleware.streaming.Stream}
 * {@link StreamBook will be the container of all the StreamRecord}
 *
 */
public class StreamRecord {

    private String streamId;
    private StreamRecordStatus streamRecordStatus = StreamRecordStatus.ALIVE;
    private BezirkZirkEndPoint recipientServiceEndPoint;
    private BezirkZirkEndPoint senderServiceEndPoint;
    private File file;
    private int recipientPort;
    private String recipientIp;
    private ZirkId zirkId;

    public StreamRecord(String streamId, BezirkZirkEndPoint recipientEndPoint, File file, BezirkZirkEndPoint senderEndPoint){
        this.streamId = streamId;
        this.recipientServiceEndPoint = recipientEndPoint;
        this.file = file;
        this.senderServiceEndPoint = senderEndPoint;
    }

    public String getStreamId() {
        return streamId;
    }

    public BezirkZirkEndPoint getRecipientServiceEndPoint() {
        return recipientServiceEndPoint;
    }

    public void setRecipientServiceEndPoint(BezirkZirkEndPoint recipientServiceEndPoint) {
        this.recipientServiceEndPoint = recipientServiceEndPoint;
    }

    public void setSenderServiceEndPoint(BezirkZirkEndPoint senderServiceEndPoint) {
        this.senderServiceEndPoint = senderServiceEndPoint;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * default will be ALIVE, but can be updated based on the staus to
     * @param streamRecordStatus status of StreamRecord
     */
    public void setStreamRecordStatus(StreamRecordStatus streamRecordStatus) {
        this.streamRecordStatus = streamRecordStatus;
    }

    public StreamRecordStatus getStreamRecordStatus() {
        return streamRecordStatus;
    }

    public void setRecipientPort(int recipientPort) {
        this.recipientPort = recipientPort;
    }

    public String getRecipientIp() {
        return recipientIp;
    }

    public void setRecipientIp(String recipientIp) {
        this.recipientIp = recipientIp;
    }

    public int getRecipientPort() {
        return recipientPort;
    }

    public void setZirkId(ZirkId zirkId) {
        this.zirkId = zirkId;
    }

    public ZirkId getZirkId() {
        return zirkId;
    }

    public BezirkZirkEndPoint getSenderServiceEndPoint() {
        return senderServiceEndPoint;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    /* Streaming Status indicates the status of the Streams.
             * ALIVE -  Status for sender, when we has initiated a request.
             * ADDRESSED   -  indicating the recipient has received the request.
             * ASSIGNED - If the Port was free for recipient, and has agreed to receive the file.
             * BUSY -  indicating the receipient is busy, All the active ports are consumed.
             * COMPLETED -  Indicates that file transfer was complete.
             * ERROR - Indicates that an error occurred during streaming.
             */
    public enum StreamRecordStatus {
        ALIVE, ADDRESSED, ASSIGNED , BUSY, COMPLETED, ERROR

    }

}
