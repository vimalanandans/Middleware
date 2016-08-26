/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 */
package com.bezirk.middleware.core.streaming.control.Objects;

import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

import java.io.File;


/**
 * This class is used as Record for BookKeeping the Streams that has been being pushed by the Services.
 */

// FIXME: 8/4/2016 Punith :: Remove StreamRecord as a Ledger.. It can be a Control message.. Look at the properties.. you may have to remove few!!!!
public class StreamRecord extends UnicastControlMessage {

    //flag to see id the DataSend needs to be encrypted
    private boolean isEncryptedStream;

    // changed after receiving the Response, this will be in PENDING status in the stream store.
    private StreamRecordStatus streamRecordStatus;

    // recipient IP, set by the proxy after getting the stream Response
    private String recipientIP;

    // recipient Port,set by the proxy after getting the stream Response
    private int recipientPort;

    //path to the file
    private File file;

    //The receipient zirk end point
    private BezirkZirkEndPoint recipientSEP;

    //This is the seal
    private String serializedStream;

    /* Streaming Status indicates the status of the Streams.
     * PENDING -  indicates the waiting to know the response
     * READY   -  indicating the recipient has agreed to receive the stream
     * BUSY    -  indicating the receipient is busy and the data cannot be streamed*/
    public enum StreamRecordStatus {
        PENDING, READY, ADDRESSED, BUSY, LOCAL
    }

    /**
     * Empty Constructor
     */
    public StreamRecord(){
        //empty constructor
    }


    /**
     * Constructor with args
     * @param sender
     * @param recipient
     * @param sphereId
     * @param discriminator
     * @param retransmit
     * @param key
     */
    public StreamRecord(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, String sphereId,
                        Discriminator discriminator, Boolean retransmit, String key){
        super(sender, recipient, sphereId, discriminator,false, key);
    }

    public StreamRecordStatus getStreamRecordStatus() {
        return streamRecordStatus;
    }

    public void setStreamRecordStatus(StreamRecordStatus streamRecordStatus) {
        this.streamRecordStatus = streamRecordStatus;
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

}
