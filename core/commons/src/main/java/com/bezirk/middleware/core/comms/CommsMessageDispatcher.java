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

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.EventLedger;
import com.bezirk.middleware.core.remotelogging.RemoteLog;
import com.bezirk.middleware.core.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Dispatches the incoming events to respective registered listener
 */
public class CommsMessageDispatcher implements MessageDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(CommsMessageDispatcher.class);

    private com.bezirk.middleware.core.comms.processor.EventMsgReceiver eventReceiver = null;

    private final RemoteLog msgLog = null;

    // Map of control receivers
    private final Map<ControlMessage.Discriminator, CtrlMsgReceiver> ctrlReceivers =
            new HashMap<>();

    public CommsMessageDispatcher() {

    }

    public void registerEventMessageReceiver(com.bezirk.middleware.core.comms.processor.EventMsgReceiver eventReceiver){
        this.eventReceiver = eventReceiver;
    }

    /**
     * register control Message receivers
     */
    @Override
    public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, CtrlMsgReceiver receiver) {
        if (ctrlReceivers.containsKey(id)) {
            logger.debug("Registration is rejected. id is already registered > " + id);
            return false; // unregister first
        }
        ctrlReceivers.put(id, receiver);
        return true;
    }
    // add unregister on need basis

    // currently sadl consumes all the zirk message. hence no registration
    // if needed extend similar mechanism to control message dispatching
    @Override
    public boolean dispatchServiceMessages(EventLedger eLedger) {
        if (ValidatorUtility.isObjectNotNull(eventReceiver)) {
            return eventReceiver.processEvent(eLedger);
        } else {
            logger.error("No Zirk event message receivers registered");
        }
        return false;
    }

    /**
     * dispatch the control message
     */
    @Override
    public boolean dispatchControlMessages(ControlMessage ctrlMsg, String serializedMsg) {

        ControlMessage.Discriminator id = ctrlMsg.getDiscriminator();

        logger.debug("Message decrypted with Discriminator : " + id);

        if(msgLog != null)
        {
            if(msgLog.isRemoteLoggingEnabled())
            {
                msgLog.sendRemoteLogToServer(ctrlMsg);
            }
        }


        //get the registered receiver
        CtrlMsgReceiver ctrlReceiver = ctrlReceivers.get(id);

        if (ValidatorUtility.isObjectNotNull(ctrlReceiver)) {
            // invoke the listener
            if (!ctrlReceiver.processControlMessage(id, serializedMsg)) {
                logger.debug("Receiver not processing id > " + id);
            }

        } else {

            logger.error("New Message / not registered ? No receiver to process id > " + id);
        }

        return true;
    }





}
