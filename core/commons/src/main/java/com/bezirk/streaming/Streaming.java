package com.bezirk.streaming;

import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.comms.Comms;
import com.bezirk.control.messages.Ledger;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.pubsubbroker.PubSubEventReceiver;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.control.Objects.StreamRecord;

import java.util.List;


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
     * Registers the stream record within stream store
     */
    boolean processStreamRecord(SendFileStreamAction streamAction, Iterable<String> sphereList);

    /**
     * set the pubSub Event receiver, this is required by the streaming module to give callbacks to respective Zirks
     * @param pubSubEventReceiver
     */
    void setEventReceiver(PubSubEventReceiver pubSubEventReceiver);
}
