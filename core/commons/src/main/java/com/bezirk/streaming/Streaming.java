package com.bezirk.streaming;

import com.bezirk.comms.Comms;
import com.bezirk.control.messages.Ledger;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.control.Objects.StreamRecord;


/**
 * This interface is introduced to separate the streaming functionalities from BezirkCommsManager.
 *
 * @author pik6kor
 */
public interface Streaming {

    /**
     * This will initialize the streaming queue, streaming thread,stream store and register the receivers with the message dispatcher.
     * and Start the streaming thread
     */
    boolean startStreams();

    /**
     * Interrupt all the streaming threads, shutdown streaming module.
     */
    boolean endStreams();

    /**
     * Interrupt a single streaming thread
     */
    boolean interruptStream(final String streamKey);

    /**
     * send the stream message based on streamId
     */
    boolean sendStream(final String streamId);

    /**
     * Registers the stream record within stream store
     */
    boolean addStreamRecordToStreamStore(final String streamId, final StreamRecord sRecord);

    /**
     * set Sphere Security. this is used for the late initalization, when a new Sphere is created. Sphere security object is updated
     */
    void setSphereSecurityForEncryption(SphereSecurity sphereSecurity);
}
