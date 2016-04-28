package com.bezirk.comms;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.remotelogging.messages.UhuLoggingMessage;
import com.bezirk.remotelogging.queues.LoggingQueueManager;
import com.bezirk.remotelogging.spherefilter.FilterLogMessages;
import com.bezirk.remotelogging.status.LoggingStatus;
import com.bezirk.remotelogging.util.Util;
import com.bezirk.sadl.ISadlEventReceiver;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vimal on 5/18/2015.
 * Dispatches the incoming events to respective registered listener
 */
public class MessageDispatcher implements IMessageDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);
    private final ISadlEventReceiver sadlEventReceiver;

    //private ISadlControlReceiver sadlCtrlRxer;
    /*
    private LogServiceMessageHandler logServiceMsgHandler = null;

    private MessageQueue msgQueue = null;

    // temp implemenetation.
    MessageQueue ctrlSenderQueue = null;
    MessageQueue streamQueue = null;
    IPortFactory  portFactory = null;
    */
    private final Date currentDate = new Date();
    // Map of control receivers
    Map<ControlMessage.Discriminator, CtrlMsgReceiver> ctrlReceivers =
            new HashMap<ControlMessage.Discriminator, CtrlMsgReceiver>();

    public MessageDispatcher(ISadlEventReceiver sadlEventReceiver) {
        this.sadlEventReceiver = sadlEventReceiver;
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
        if (BezirkValidatorUtility.isObjectNotNull(sadlEventReceiver)) {
            return sadlEventReceiver.processEvent(eLedger);
        } else {
            logger.error("no valid zirk message receivers ");
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

        if (LoggingStatus.isLoggingEnabled() && FilterLogMessages.checkSphere(ctrlMsg.getSphereId())) {
            sendRemoteLogMessage(ctrlMsg);
        }


        //get the registered receiver
        CtrlMsgReceiver ctrlReceiver = ctrlReceivers.get(id);

        if (BezirkValidatorUtility.isObjectNotNull(ctrlReceiver)) {
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
        if (tcMessage.getIsMessageFromHost()) { //If the msg is local : set serialized msg
            tcMessage.setSerializedMessage(tcMessage.getMessage().serialize());
        }
        ControlMessage msg = tcMessage.getMessage();
        logger.debug("Message decrypted with Discriminator : " + msg.getDiscriminator());

        if (LoggingStatus.isLoggingEnabled() && FilterLogMessages.checkSphere(tcMessage.getSphereId())) {
            sendRemoteLogMessage(tcMessage);
        }

        ControlMessage.Discriminator id = msg.getDiscriminator();

        //get the registered receiver
        CtrlMsgReceiver ctrlReceiver = ctrlReceivers.get(id);

        if (BezirkValidatorUtility.isObjectNotNull(ctrlReceiver)) {
            // invoke the listener
            if (!ctrlReceiver.processControlMessage(id, tcMessage.getSerializedMessage())) {
                logger.debug("Receiver not processing id > " + id);
            }

        } else {

            logger.error("New Message / not registered ? No receiver to process id > " + id);
        }

        return true;
    }


    private void sendRemoteLogMessage(ControlLedger tcMessage) {
        try {
            LoggingQueueManager.loadLogSenderQueue(
                    new UhuLoggingMessage(
                            tcMessage.getSphereId(),
                            String.valueOf(currentDate.getTime()),
                            UhuCompManager.getUpaDevice().getDeviceName(),
                            Util.CONTROL_RECEIVER_VALUE,
                            tcMessage.getMessage().getUniqueKey(),
                            tcMessage.getMessage().getDiscriminator().name(),
                            Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name(),
                            Util.LOGGING_VERSION).serialize());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    private void sendRemoteLogMessage(ControlMessage msg) {
        try {
            LoggingQueueManager.loadLogSenderQueue(
                    new UhuLoggingMessage(
                            msg.getSphereId(),
                            String.valueOf(currentDate.getTime()),
                            UhuCompManager.getUpaDevice().getDeviceName(),
                            Util.CONTROL_RECEIVER_VALUE,
                            msg.getUniqueKey(),
                            msg.getDiscriminator().name(),
                            Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name(),
                            Util.LOGGING_VERSION).serialize());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

}
