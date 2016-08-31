package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.actions.SendFileStreamAction;
import com.bezirk.middleware.core.comms.processor.EventMsgReceiver;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.EventLedger;
import com.bezirk.middleware.core.control.messages.Ledger;

/**
 * This class is created to de-couple from the rest of bezirk (spheres / sadl / stream)
 * <p>
 * Also this would help to loop back the communication of virtual spheres
 * for junit testing. this is created to replace the 'static' MessageQueueManager
 * Re-factor, extend and rename the below based on future need
 * </p>
 */
public interface Comms {

//    /**
//     * start the communication
//     */
//    boolean startComms();
//
//    /**
//     * stop the communication
//     */
//    boolean stopComms();
//
//    /**
//     * close the communication
//     */
//    boolean closeComms();
//
//    /**
//     * restart the underlying comms
//     */
//    boolean restartComms();

    /**
     * Set the sphere for sadl. for late initialization
     *//*
    void setSphereSecurity(final SphereSecurity sphereSecurity);*/

    /**
     * TODO: Split the interface for controlling comms component as CommsCtrl
     * and below access related as Comms
     * */

    /**
     * send the control or event message depends of ledger type
     */
    @Deprecated // Use sendEventLedger, or sendControlLedger
    boolean sendMessage(Ledger message);

    /**
     * Send event ledger
     * */
    boolean sendEventLedger(EventLedger eventLedger);

    boolean sendControlLedger(ControlLedger controlLedger);

    /**
     * Send event ledger
     * */
    boolean sendControlMessage(ControlMessage message);

    /**
     * send the stream message based on unique key
     *//*
    boolean sendStream(String uniqueKey);*/

    /**
     * fixme write this
     * @param streamAction
     * @param sphereList
     * @return
     */
    boolean processStreamRecord(SendFileStreamAction streamAction, Iterable<String> sphereList);

    /**
     *
     * @param notification
     * @return
     */
    boolean registerNotification(CommsNotification notification);

    /** this is on each comms instance returns its own created id */
    String getNodeId();

    /**
     * Initialize the communications
     * creates queues, threads, sockets
     **/
//    boolean initComms(CommsProperties commsProperties, InetAddress addr,
//                      SphereSecurity sphereSecurity,
//                      Streaming streaming);

    boolean registerControlMessageReceiver(ControlMessage.Discriminator id, CtrlMsgReceiver receiver);

    /* register event message receiver */
    boolean registerEventMessageReceiver(EventMsgReceiver receiver);



}

