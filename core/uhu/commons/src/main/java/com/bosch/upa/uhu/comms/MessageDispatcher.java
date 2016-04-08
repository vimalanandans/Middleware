package com.bosch.upa.uhu.comms;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.EventLedger;
import com.bosch.upa.uhu.remotelogging.messages.UhuLoggingMessage;
import com.bosch.upa.uhu.remotelogging.queues.LoggingQueueManager;
import com.bosch.upa.uhu.remotelogging.spherefilter.FilterLogMessages;
import com.bosch.upa.uhu.remotelogging.status.LoggingStatus;
import com.bosch.upa.uhu.remotelogging.util.Util;
import com.bosch.upa.uhu.sadl.ISadlEventReceiver;
import com.bosch.upa.uhu.util.UhuValidatorUtility;

/**
 * Created by Vimal on 5/18/2015.
 * Dispatches the incoming events to respective registered listener
 */
public class MessageDispatcher implements IMessageDispatcher{

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcher.class);

    // Map of control receivers
    Map<ControlMessage.Discriminator, ICtrlMsgReceiver> ctrlReceivers =
            new HashMap <ControlMessage.Discriminator, ICtrlMsgReceiver>();

   //private ISadlControlReceiver sadlCtrlRxer;

    private final ISadlEventReceiver sadlEventReceiver;
    /*
    private LogServiceMessageHandler logServiceMsgHandler = null;

    private MessageQueue msgQueue = null;

    // temp implemenetation.
    MessageQueue ctrlSenderQueue = null;
    MessageQueue streamQueue = null;
    IPortFactory  portFactory = null;
    */
    private final Date currentDate = new Date();

    public MessageDispatcher(ISadlEventReceiver sadlEventReceiver){
        this.sadlEventReceiver = sadlEventReceiver;
    }

    /** register control Message receivers */
    @Override
    public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, ICtrlMsgReceiver receiver){
        if(ctrlReceivers.containsKey(id))
        {
            log.debug("Registration is rejected. id is already registered > "+id);
            return false; // unregister first
        }
        ctrlReceivers.put(id, receiver);
        return true;
    }
    // add unregister on need basis

    // currently sadl consumes all the service message. hence no registration
    // if needed extend similar mechanism to control message dispatching
    @Override
    public boolean dispatchServiceMessages(EventLedger eLedger)
    {
        if(UhuValidatorUtility.isObjectNotNull(sadlEventReceiver)) {
            return sadlEventReceiver.processEvent(eLedger);
        }
        else{
            log.error("no valid service message receivers ");
        }
        return false;
    }

    /** dispatch the control message */
    @Override
    public boolean dispatchControlMessages(ControlMessage ctrlMsg, String serializedMsg){

        ControlMessage.Discriminator id = ctrlMsg.getDiscriminator();

        log.debug("Message decrypted with Discriminator : " + id);

        if(LoggingStatus.isLoggingEnabled() && FilterLogMessages.checkSphere(ctrlMsg.getSphereId())){
            sendRemoteLogMessage(ctrlMsg);
        }



        //get the registered receiver
        ICtrlMsgReceiver ctrlReceiver = ctrlReceivers.get(id);

        if(UhuValidatorUtility.isObjectNotNull(ctrlReceiver))
        {
            // invoke the listener
            if(!ctrlReceiver.processControlMessage(id, serializedMsg))
            {
                log.debug("Receiver not processing id > " + id);
            }

        }else{

            log.error("New Message / not registered ? No receiver to process id > "+id);
        }

        return true;
    }

    /** dispatch the control message */
    @Override
    public boolean dispatchControlMessages(ControlLedger tcMessage){
        if(tcMessage.getIsMessageFromHost()){ //If the msg is local : set serialized msg
            tcMessage.setSerializedMessage(tcMessage.getMessage().serialize());
        }
        ControlMessage msg = (ControlMessage)tcMessage.getMessage();
        log.debug("Message decrypted with Discriminator : " + msg.getDiscriminator());

        if(LoggingStatus.isLoggingEnabled() && FilterLogMessages.checkSphere(tcMessage.getSphereId())){
            sendRemoteLogMessage(tcMessage);
        }

        ControlMessage.Discriminator id = msg.getDiscriminator();

        //get the registered receiver
        ICtrlMsgReceiver ctrlReceiver = ctrlReceivers.get(id);

        if(UhuValidatorUtility.isObjectNotNull(ctrlReceiver))
        {
            // invoke the listener
            if(!ctrlReceiver.processControlMessage(id, tcMessage.getSerializedMessage()))
            {
                log.debug("Receiver not processing id > " + id);
            }

        }else{

            log.error("New Message / not registered ? No receiver to process id > "+id);
        }

        return true;
    }



    private void sendRemoteLogMessage(ControlLedger tcMessage){
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
            log.error(e.getMessage());
        }
    }

    private void sendRemoteLogMessage(ControlMessage msg){
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
            log.error(e.getMessage());
        }
    }

}
