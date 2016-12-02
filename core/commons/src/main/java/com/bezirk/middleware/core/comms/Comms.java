/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.comms;

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
    @Deprecated
    // Use sendEventLedger, or sendControlLedger
    boolean sendMessage(Ledger message);

    /**
     * Send event ledger
     */
    boolean sendEventLedger(EventLedger eventLedger);

    boolean sendControlLedger(ControlLedger controlLedger);

    /**
     * Send event ledger
     */
    boolean sendControlMessage(ControlMessage message);

    /**
     * @param notification
     * @return
     */
    boolean registerNotification(CommsNotification notification);

    /**
     * this is on each comms instance returns its own created id
     */
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

