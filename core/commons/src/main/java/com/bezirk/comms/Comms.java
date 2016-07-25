package com.bezirk.comms;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.Ledger;

import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.control.Objects.StreamRecord;

import java.net.InetAddress;

/**
 * This class is created to de-couple from the rest of bezirk (spheres / sadl / stream)
 * <p>
 * Also this would help to loop back the communication of virtual spheres
 * for junit testing. this is created to replace the 'static' MessageQueueManager
 * Re-factor, extend and rename the below based on future need
 * </p>
 */
public interface Comms {

    /**
     * start the communication
     */
    boolean startComms();

    /**
     * stop the communication
     */
    boolean stopComms();

    /**
     * close the communication
     */
    boolean closeComms();

    /**
     * restart the underlying comms
     */
    boolean restartComms();

    /**
     * send the control or event message depends of ledger type
     */
    boolean sendMessage(Ledger message);

    /**
     * send the stream message based on unique key
     */
    boolean sendStream(String uniqueKey);

    boolean registerStreamBook(String key, StreamRecord sRecord);

    boolean registerNotification(CommsNotification notification);

    /**
     * Initialize the communications
     * creates queues, threads, sockets
     **/
    boolean initComms(CommsProperties commsProperties, InetAddress addr,
                      PubSubBroker pubSubBroker, SphereSecurity sphereSecurity,
                      com.bezirk.streaming.Streaming streaming);

    boolean registerControlMessageReceiver(ControlMessage.Discriminator id, CtrlMsgReceiver receiver);

    /**
     * Set the sphere for sadl. for late initialization
     */
    void setSphereSecurity(final SphereSecurity sphereSecurity);

}

