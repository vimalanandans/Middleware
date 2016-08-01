/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 */
package com.bezirk.streaming.control.Objects;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

import java.io.File;
import java.io.PipedInputStream;


/**
 * This class is used as Record for BookKeeping the Streams that has been being pushed by the Services.
 */
public class StreamRecord extends com.bezirk.control.messages.Ledger {
    //private short localStreamId;
    private BezirkZirkEndPoint senderSEP;
    private boolean isEncryptedStream;                    // if the DataSend needs to be encrypted
    private String sphereId;                        // used for sending the data , set by the sender
    private StreamingStatus streamStatus;        // changed after receiving the Response
    private String recipientIP;                    // recipient IP, set by the proxy after getting the stream Response
    private int recipientPort;                    // recipient Port,set by the proxy after getting the stream Response
    private File file;                        // path to the file
    private BezirkZirkEndPoint recipientSEP;    // Used for Local streaming.
    private String serializedStream;                // USed for Local Zirk to Zirk Streaming
    private String streamRequestKey;            // sream request key is the unique identifier for the streaming.

    /* Streaming Status indicates the status of the Streams.
     * PENDING -  indicates the waiting to know the response
     * READY   -  indicating the recipient has agreed to receive the stream
     * BUSY    -  indicating the receipient is busy and the data cannot be streamed*/
    public enum StreamingStatus {
        PENDING, READY, ADDRESSED, BUSY, LOCAL
    }

    public BezirkZirkEndPoint getSenderSEP() {
        return senderSEP;
    }

    public void setSenderSEP(BezirkZirkEndPoint senderSEP) {
        this.senderSEP = senderSEP;
    }

    public String getSphereId() {
        return sphereId;
    }

    public void setSphereId(String sphereId) {
        this.sphereId = sphereId;
    }

    public StreamingStatus getStreamStatus() {
        return streamStatus;
    }

    public void setStreamStatus(StreamingStatus streamStatus) {
        this.streamStatus = streamStatus;
    }

    public String getRecipientIP() {
        return recipientIP;
    }

    public void setRecipientIP(String recipientIP) {
        this.recipientIP = recipientIP;
    }

    public int getRecipientPort() {
        return recipientPort;
    }

    public void setRecipientPort(int recipientPort) {
        this.recipientPort = recipientPort;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public BezirkZirkEndPoint getRecipientSEP() {
        return recipientSEP;
    }

    public void setRecipientSEP(BezirkZirkEndPoint recipientSEP) {
        this.recipientSEP = recipientSEP;
    }

    public String getSerializedStream() {
        return serializedStream;
    }

    public void setSerializedStream(String serializedStream) {
        this.serializedStream = serializedStream;
    }

    public boolean isEncryptedStream() {
        return isEncryptedStream;
    }

    public void setEncryptedStream(boolean encryptedStream) {
        isEncryptedStream = encryptedStream;
    }

    public String getStreamRequestKey() {
        return streamRequestKey;
    }

    public void setStreamRequestKey(String streamRequestKey) {
        this.streamRequestKey = streamRequestKey;
    }
}
