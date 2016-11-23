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

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

import java.io.File;

/**
 * StreamRecord, This will hold complete metadata of the streaming information.
 * This will be kept in the StreamBook
 *
 * Created by PIK6KOR on 11/10/2016.
 */

public class StreamRecord {

    //stream primary ID
    private Short streamId;

    // changed after receiving the Response, this will be in ALIVE status in the stream store.
    private StreamRecordStatus streamRecordStatus = StreamRecordStatus.ALIVE;

    //The receipient zirk end point
    private BezirkZirkEndPoint recipientSEP;

    //File file information which will be sent
    private File file;

    // recipient Port,set after getting the Stream Response
    private Integer recipientPort;

    // recipient Port,set after getting the Stream Response
    private String recipientIp;

    public StreamRecord(Short streamId, ZirkEndPoint endPoint, File file){
        this.streamId = streamId;
        this.recipientSEP = (BezirkZirkEndPoint) endPoint;
        this.file = file;
    }

    public Short getStreamId() {
        return streamId;
    }

    BezirkZirkEndPoint getRecipientSEP() {
        return recipientSEP;
    }

    public File getFile() {
        return file;
    }

    /**
     * default will be PENDING, but can be updated based on the staus to
     * @param streamRecordStatus status of StreamRecord
     */
    public void setStreamRecordStatus(StreamRecordStatus streamRecordStatus) {
        this.streamRecordStatus = streamRecordStatus;
    }

    public StreamRecordStatus getStreamRecordStatus() {
        return streamRecordStatus;
    }

    public void setRecipientPort(Integer recipientPort) {
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

    /* Streaming Status indicates the status of the Streams.
             * ALIVE -  Status for sender, when we has initiated a request.
             * ADDRESSED   -  indicating the recipient has received the request.
             * ASSIGNED - If the Port was free for recipient, and has agreed to receive the file.
             * BUSY -  indicating the receipient is busy, All the active ports are consumed.
             * COMPLETED -  Indicates that file transfer was complete.
             * */
    public enum StreamRecordStatus {
        ALIVE, ADDRESSED, ASSIGNED , BUSY, COMPLETED

    }


}
