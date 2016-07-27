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
    public short localStreamId;
    public BezirkZirkEndPoint senderSEP;
    public boolean isIncremental;                // used for sending the data, set by the sender
    public boolean isReliable;                    // used for sending the data, set by the sender
    public boolean isEncrypted;                    // if the DataSend needs to be encrypted
    public String sphere;                        // used for sending the data , set by the sender
    public StreamingStatus streamStatus;        // changed after receiving the Response
    public String recipientIP;                    // recipient IP, set by the proxy after getting the stream Response
    public int recipientPort;                    // recipient Port,set by the proxy after getting the stream Response
    public PipedInputStream pipedInputStream;    // set if it is unreliable
    public File file;                        // path to the file
    public BezirkZirkEndPoint recipientSEP;    // Used for Local streaming.
    public String serializedStream;                // USed for Local Streaming
    /* Streaming Status indicates the status of the Streams.
     * PENDING -  indicates the waiting to know the response
     * READY   -  indicating the recipient has agreed to receive the stream
     * BUSY    -  indicating the receipient is busy and the data cannot be streamed*/
    public enum StreamingStatus {
        PENDING, READY, ADDRESSED, BUSY, LOCAL
    }
}
