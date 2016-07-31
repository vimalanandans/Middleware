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
    private short localStreamId;
    private BezirkZirkEndPoint senderSEP;
    //private boolean isRealTimeStream;                // used to notify that this is a realtime processed streaming.
    //private boolean isReliable;                    // used for sending the data, set by the sender
    private boolean isEncryptedStream;                    // if the DataSend needs to be encrypted
    private String sphere;                        // used for sending the data , set by the sender
    private StreamingStatus streamStatus;        // changed after receiving the Response
    private String recipientIP;                    // recipient IP, set by the proxy after getting the stream Response
    private int recipientPort;                    // recipient Port,set by the proxy after getting the stream Response
    //private PipedInputStream pipedInputStream;    // set if it is unreliable
    private File file;                        // path to the file
    private BezirkZirkEndPoint recipientSEP;    // Used for Local streaming.
    private String serializedStream;                // USed for Local Zirk to Zirk Streaming
    private String streamTopic;                    // USed for Local Zirk to Zirk Streaming
    private String streamRequestKey;            // sream request key is the unique identifier for the streaming.

    /* Streaming Status indicates the status of the Streams.
     * PENDING -  indicates the waiting to know the response
     * READY   -  indicating the recipient has agreed to receive the stream
     * BUSY    -  indicating the receipient is busy and the data cannot be streamed*/
    public enum StreamingStatus {
        PENDING, READY, ADDRESSED, BUSY, LOCAL
    }

    public short getLocalStreamId() {
        return localStreamId;
    }

    public void setLocalStreamId(short localStreamId) {
        this.localStreamId = localStreamId;
    }

    public BezirkZirkEndPoint getSenderSEP() {
        return senderSEP;
    }

    public void setSenderSEP(BezirkZirkEndPoint senderSEP) {
        this.senderSEP = senderSEP;
    }

    /*public boolean isIncremental() {
        return isIncremental;
    }

    public void setIncremental(boolean incremental) {
        isIncremental = incremental;
    }
    */
    /*public boolean isReliable() {
        return isReliable;
    }

    public void setReliable(boolean reliable) {
        isReliable = reliable;
    }*/

    /*public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }*/

    public String getSphere() {
        return sphere;
    }

    public void setSphere(String sphere) {
        this.sphere = sphere;
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

    /*public PipedInputStream getPipedInputStream() {
        return pipedInputStream;
    }

    public void setPipedInputStream(PipedInputStream pipedInputStream) {
        this.pipedInputStream = pipedInputStream;
    }*/

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

    public String getStreamTopic() {
        return streamTopic;
    }

    public void setStreamTopic(String streamTopic) {
        this.streamTopic = streamTopic;
    }

    /*public boolean isRealTimeStream() {
        return isRealTimeStream;
    }

    public void setRealTimeStream(boolean realTimeStream) {
        isRealTimeStream = realTimeStream;
    }*/

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
