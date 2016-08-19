package com.bezirk.comms;

import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.comms.processor.EventMsgReceiver;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;

import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.control.Objects.StreamRecord;

import java.util.List;

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

    /**
     * Initialize the communications
     * creates queues, threads, sockets
     **/
//    boolean initComms(CommsProperties commsProperties, InetAddress addr,
//                      SphereSecurity sphereSecurity,
//                      com.bezirk.streaming.Streaming streaming);

    boolean registerControlMessageReceiver(ControlMessage.Discriminator id, CtrlMsgReceiver receiver);

    /* register event message receiver */
    boolean registerEventMessageReceiver(EventMsgReceiver receiver);



}

