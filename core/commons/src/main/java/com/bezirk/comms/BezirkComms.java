package com.bezirk.comms;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.Ledger;

import com.bezirk.pipe.PipeManager;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.sphere.api.BezirkSphereForPubSub;
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
public interface BezirkComms {

    /**
     * start the communication
     */
    public boolean startComms();

    /**
     * stop the communication
     */
    public boolean stopComms();

    /**
     * close the communication
     */
    public boolean closeComms();

    /**
     * restart the underlying comms
     */
    public boolean restartComms();

    /**
     * send the control or event message depends of ledger type
     */
    public boolean sendMessage(Ledger message);

    /**
     * send the stream message based on unique key
     */
    public boolean sendStream(String uniqueKey);

    public boolean registerStreamBook(String key, StreamRecord sRecord);

    public boolean registerNotification(CommsNotification notification);

    /**
     * Initialize the communications
     * creates queues, threads, sockets
     **/
    public boolean initComms(CommsProperties commsProperties, InetAddress addr,
                             PubSubBroker pubSubBroker, PipeManager pipe,
                             com.bezirk.streaming.Streaming streaming);

    public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, CtrlMsgReceiver receiver);

    /**
     * Set the sphere for sadl. for late initialization
     */
    public void setSphereForSadl(final BezirkSphereForPubSub bezirkSphere);

}

