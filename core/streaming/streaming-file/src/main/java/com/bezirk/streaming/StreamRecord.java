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

    // recipient Port,set after getting the Stream Response
    private String recipientIp;

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
