package com.bezirk.comms;

import com.bezirk.comms.processor.EventMsgReceiver;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.remotelogging.RemoteLog;

import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vimal on 5/18/2015.
 * Dispatches the incoming events to respective registered listener
 */
public class CommsMessageDispatcher implements MessageDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(CommsMessageDispatcher.class);

    private EventMsgReceiver eventReceiver = null;

    private RemoteLog msgLog = null;

    // Map of control receivers
    private Map<ControlMessage.Discriminator, CtrlMsgReceiver> ctrlReceivers =
            new HashMap<>();

    public CommsMessageDispatcher() {

    }

    public void registerEventMessageReceiver(EventMsgReceiver eventReceiver){
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
            if(msgLog.isEnabled())
            {
                msgLog.sendRemoteLogMessage(ctrlMsg);
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

    /**
     * dispatch the control message
     */
    @Override
    public boolean dispatchControlMessages(ControlLedger tcMessage) {
        //FIXME : setIsMessageFromHost is never called. validate and remove the below
        if (tcMessage.getIsMessageFromHost()) { //If the msg is local : set serialized msg
            tcMessage.setSerializedMessage(tcMessage.getMessage().serialize());
        }
        ControlMessage msg = tcMessage.getMessage();
        logger.debug("Message decrypted with Discriminator : " + msg.getDiscriminator());

        if(msgLog != null)
        {
            if(msgLog.isEnabled()) {
                msgLog.sendRemoteLogMessage(tcMessage);
            }
        }

        ControlMessage.Discriminator id = msg.getDiscriminator();

        //get the registered receiver
        CtrlMsgReceiver ctrlReceiver = ctrlReceivers.get(id);

        if (ValidatorUtility.isObjectNotNull(ctrlReceiver)) {
            // invoke the listener
            if (!ctrlReceiver.processControlMessage(id, tcMessage.getSerializedMessage())) {
                logger.debug("Receiver not processing id > " + id);
            }

        } else {

            logger.error("New Message / not registered ? No receiver to process id > " + id);
        }

        return true;
    }




}
