package com.bezirk.streaming;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

import java.io.File;

/**
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

    public StreamRecord(Short streamId, BezirkZirkEndPoint endPoint, File file){
        this.streamId = streamId;
        this.recipientSEP = endPoint;
        this.file = file;
    }

    public Short getStreamId() {
        return streamId;
    }

    public BezirkZirkEndPoint getRecipientSEP() {
        return recipientSEP;
    }

    public File getFile() {
        return file;
    }

    /**
     * default will be PENDING, but can be updated based on the staus to
     * @param streamRecordStatus
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

    public int getRecipientPort() {
        return recipientPort;
    }

    /* Streaming Status indicates the status of the Streams.
             * ALIVE -  indicates the waiting to know the response
             * ADDRESSED   -  indicating the recipient has agreed to receive the stream
             * BUSY    -  indicating the receipient is busy and the data cannot be streamed*/
    public enum StreamRecordStatus {
        //PENDING, READY, BUSY, LOCAL
        ALIVE, ADDRESSED, ASSIGNED , PROCESSING, BUSY, COMPLETED

    }


}
