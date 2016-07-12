package com.bezirk.remotelogging;

import com.bezirk.BezirkCompManager;
import com.bezirk.comms.BezirkComms;
import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;

import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.logging.LoggingServiceMessage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by Vimal on 7/11/2016.
 *
 */
public class RemoteMessageLogManager implements RemoteMessageLog {

    private static final Logger logger = LoggerFactory.getLogger(RemoteMessageLogManager.class);
    private final Date currentDate = new Date();

    LogServiceMessageHandler logServiceMsgHandler = null;
    BezirkComms comms;

    //private BezirkCallback bezirkCallback = null;
    CommCtrlReceiver ctrlReceiver = new CommCtrlReceiver();


    @Override
    public boolean  initRemoteLogger(BezirkComms comms) {
        // register the logging zirk message
        comms.registerControlMessageReceiver(ControlMessage.Discriminator.LoggingServiceMessage, ctrlReceiver);
        this.comms = comms;

        return true;
    }

    @Override
    public boolean setLogger(boolean enable, String[] sphereNameList) {
        String[] loggingSpheres;
        if (RemoteMessageLog.ALL_SPHERES.equals(sphereNameList)) {
            loggingSpheres = new String[1];
            loggingSpheres[0] = RemoteMessageLog.ALL_SPHERES;
        } else {
            loggingSpheres = sphereNameList;
        }

        LogServiceActivatorDeactivator.sendLoggingServiceMsgToClients(comms,
                sphereNameList, loggingSpheres, enable);
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean sendRemoteLogMessage(ControlLedger tcMessage) {

        if(FilterLogMessages.checkSphere(tcMessage.getSphereId()))
        {

            try {
                LoggingQueueManager.loadLogSenderQueue(
                        new RemoteLoggingMessage(
                                tcMessage.getSphereId(),
                                String.valueOf(currentDate.getTime()),
                                BezirkCompManager.getUpaDevice().getDeviceName(),
                                Util.CONTROL_RECEIVER_VALUE,
                                tcMessage.getMessage().getUniqueKey(),
                                tcMessage.getMessage().getDiscriminator().name(),
                                Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name(),
                                Util.LOGGING_VERSION).serialize());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean sendRemoteLogMessage(ControlMessage msg) {
        if(FilterLogMessages.checkSphere(msg.getSphereId()))
        {
            try {
                LoggingQueueManager.loadLogSenderQueue(
                        new RemoteLoggingMessage(
                                msg.getSphereId(),
                                String.valueOf(currentDate.getTime()),
                                BezirkCompManager.getUpaDevice().getDeviceName(),
                                Util.CONTROL_RECEIVER_VALUE,
                                msg.getUniqueKey(),
                                msg.getDiscriminator().name(),
                                Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name(),
                                Util.LOGGING_VERSION).serialize());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean sendRemoteLogMessage(EventLedger eLedger) {
        if(FilterLogMessages.checkSphere(eLedger.getHeader().getSphereName())) {
            try {
                LoggingQueueManager.loadLogSenderQueue(new RemoteLoggingMessage(eLedger.getHeader().getSphereName(),
                        String.valueOf(currentDate.getTime()), BezirkCompManager.getUpaDevice().getDeviceName(),
                        Util.CONTROL_RECEIVER_VALUE, eLedger.getHeader().getUniqueMsgId(), eLedger.getHeader().getTopic(),
                        Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_RECEIVE.name(), Util.LOGGING_VERSION).serialize());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
            return true;
        }
        return false;
    }

    @Override
    /**
     * TODO: test the below logic
     * */
    public boolean isRemoteMessageValid(RemoteLoggingMessage logMessage) {
        if(logMessage.typeOfMessage
                .equals(Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name())
                || logMessage.typeOfMessage
                .equals(Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_SEND.name())) {
            return false;
        }
        return false;
    }


    class CommCtrlReceiver implements CtrlMsgReceiver {
        @Override
        // FIXME : remove the below Log related quickfix, by moving the implementation to respective module
        public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
            switch (id) {
                case LoggingServiceMessage:
                    //logger.debug("<<<<<<<<  LOGGING MESSAGE RECEIVED FROM LOGGING SERVICE  >>>>>>>>>");
                    logger.debug("ReceivedLogMessage-> " + serializedMsg);
                    try {
                        final LoggingServiceMessage loggingServiceMsg = ControlMessage.deserialize(serializedMsg, LoggingServiceMessage.class);

                        if (null == logServiceMsgHandler) {
                            logServiceMsgHandler = new LogServiceMessageHandler();
                        }
                        logServiceMsgHandler.handleLogServiceMessage(loggingServiceMsg);
                    } catch (Exception e) {
                        logger.error("Error in Deserializing LogServiceMessage", e);
                    }
                    break;
                default:
                    logger.error("Unknown control message > " + id);
                    return false;
            }
            return true;
        }
    }

}
