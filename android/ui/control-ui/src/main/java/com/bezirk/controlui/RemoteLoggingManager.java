package com.bezirk.controlui;


import com.bezirk.comms.Comms;
import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.comms.ZyreCommsManager;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.device.Device;
import com.bezirk.networking.NetworkManager;
import com.bezirk.remotelogging.RemoteLog;
import com.bezirk.remotelogging.RemoteLoggingMessage;
import com.bezirk.remotelogging.RemoteLoggingMessageNotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Logging Manager class that starts/stops LoggingServices and LoggingClient.
 * The platforms need to instantiate this manager and can start stop the zirk.
 */
public  class RemoteLoggingManager implements RemoteLog {

    /**
     * RemoteLoggingService
     */
    private RemoteLoggingService remoteLoggingService = null;
    /**
     * LogReceiverProcessor used by the Logging Zirk
     */
    private ReceiverQueueProcessor receiverQueueProcessor = null;
    /**
     * Logging Client
     */
    private LoggingClient logClient = null;

    private static final Logger logger = LoggerFactory.getLogger(RemoteLoggingManager.class);

    private final Date currentDate = new Date();

    Device device;

     NetworkManager networkManager;
    public RemoteLoggingManager(){
        networkManager = null;
    }
    private boolean sendLoggingeMsgToClients=false;

    public RemoteLoggingManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }



    ServiceMessageHandler logServiceMsgHandler = null;
    Comms comms;

    //private BezirkCallback bezirkCallback = null;
    CommCtrlReceiver ctrlReceiver = new CommCtrlReceiver();


    /*@Override
    public boolean initRemoteLogger(Comms comms, Device device) {
        // register the logging zirk message
        comms.registerControlMessageReceiver(ControlMessage.Discriminator.LoggingServiceMessage, ctrlReceiver);
        this.comms = comms;
        this.device = device;
        return true;
    }*/

    @Override
    public boolean enableLogging(boolean enable, String[] sphereNameList) {
        logger.debug("enableLogging method in control ui");
        ZyreCommsManager zyreCommsManager = new ZyreCommsManager(networkManager);
        comms=zyreCommsManager;
        String[] loggingSpheres;
        if (RemoteLoggingConfig.ALL_SPHERES.equals(sphereNameList)) {
            loggingSpheres = new String[1];
            loggingSpheres[0] = RemoteLoggingConfig.ALL_SPHERES;
        } else {
            loggingSpheres = sphereNameList;
        }

        sendLoggingeMsgToClients = ServiceActivatorDeactivator.sendLoggingServiceMsgToClients(comms,
                sphereNameList, loggingSpheres, enable, networkManager);
        return sendLoggingeMsgToClients;
    }

    @Override
    public boolean isRemoteLoggingEnabled() {
        if(sendLoggingeMsgToClients==true){
            return true;
        }else{
            return false;
        }

    }


    @Override
    public boolean sendRemoteLogLedgerMessage(Ledger ledger) {
        boolean returnValue=true;
        if(ledger instanceof EventLedger){
            try {
                LoggingQueueManager.loadLogSenderQueue(new RemoteLoggingMessage(((EventLedger) ledger).getHeader().getSphereName(),
                        String.valueOf(currentDate.getTime()), device.getDeviceName(),
                        Util.CONTROL_RECEIVER_VALUE, ((EventLedger) ledger).getHeader().getUniqueMsgId(),
                        Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_RECEIVE.name(), Util.LOGGING_VERSION).serialize());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                return false;
            }
            return returnValue;
        }else if(ledger instanceof ControlLedger){
            if(FilterLogMessages.checkSphere(((ControlLedger) ledger).getSphereId()))
            {

                try {
                    LoggingQueueManager.loadLogSenderQueue(
                            new RemoteLoggingMessage(
                                    ((ControlLedger) ledger).getSphereId(),
                                    String.valueOf(currentDate.getTime()),
                                    device.getDeviceName(),
                                    Util.CONTROL_RECEIVER_VALUE,
                                    ((ControlLedger) ledger).getMessage().getUniqueKey(),
                                    Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name(),
                                    Util.LOGGING_VERSION).serialize());
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                    return false;
                }

                return returnValue;
            }
            return false;
        }
        return returnValue;
    }

    /*@Override
    public boolean sendRemoteLogMessage(ControlLedger tcMessage) {

        if (FilterLogMessages.checkSphere(tcMessage.getSphereId())) {

            try {
                LoggingQueueManager.loadLogSenderQueue(
                        new RemoteLoggingMessage(
                                tcMessage.getSphereId(),
                                String.valueOf(currentDate.getTime()),
                                device.getDeviceName(),
                                Util.CONTROL_RECEIVER_VALUE,
                                tcMessage.getMessage().getUniqueKey(),
                                Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name(),
                                Util.LOGGING_VERSION).serialize());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                return false;
            }

            return true;
        }
        return false;
    }*/

    @Override
    public boolean sendRemoteLogControlMessage(ControlMessage msg) {
        if (FilterLogMessages.checkSphere(msg.getSphereId())) {
            try {
                LoggingQueueManager.loadLogSenderQueue(
                        new RemoteLoggingMessage(
                                msg.getSphereId(),
                                String.valueOf(currentDate.getTime()),
                                device.getDeviceName(),
                                Util.CONTROL_RECEIVER_VALUE,
                                msg.getUniqueKey(),
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

   /* @Override
    public boolean sendRemoteLogMessage(EventLedger eLedger) {
        try {
            LoggingQueueManager.loadLogSenderQueue(new RemoteLoggingMessage(eLedger.getHeader().getSphereName(),
                    String.valueOf(currentDate.getTime()), device.getDeviceName(),
                    Util.CONTROL_RECEIVER_VALUE, eLedger.getHeader().getUniqueMsgId(),
                    Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_RECEIVE.name(), Util.LOGGING_VERSION).serialize());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }*/


   /* @Override
    *//**
     * TODO: test the below logic
     * *//*
    public boolean isRemoteMessageValid(RemoteLoggingMessage logMessage) {
        if (logMessage.typeOfMessage
                .equals(Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name())
                || logMessage.typeOfMessage
                .equals(Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_SEND.name())) {
            return false;
        }
        return false;
    }*/


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
                            logServiceMsgHandler = new ServiceMessageHandler(networkManager);
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

    /**
     * Starts the Logging Zirk
     *
     * @param platformSpecificHandler handler to give callback once the zirk receives the request
     * @throws Exception if handler is null, or something goes wrong while processing.
     */
    @Override
    public boolean startRemoteLoggingService(final int loggingPort,final RemoteLoggingMessageNotification platformSpecificHandler) {
        logger.debug("loggingPort is "+loggingPort);
        if (remoteLoggingService == null && platformSpecificHandler != null) {
            logger.debug("remoteLoggingService and platformSpecificHandler is not null ");
            remoteLoggingService = new RemoteLoggingService(ServiceActivatorDeactivator.REMOTE_LOGGING_PORT);
            receiverQueueProcessor = new ReceiverQueueProcessor(platformSpecificHandler);
            try {
                remoteLoggingService.startRemoteLoggingService();
                receiverQueueProcessor.startProcessing();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }else{
            logger.debug("any one of them is null");
        }
        //throw new Exception("Tried to start LoggingService again, that is already started or Handler is null");
        return false;
    }

    /**
     * Stops the logging Zirk
     *
     * @throws Exception if logging zirk is tried to stop that is not started
     */
    @Override
    public boolean stopRemoteLoggingService() {
        if (remoteLoggingService != null) {
            try {
                receiverQueueProcessor.stopProcessing();
                remoteLoggingService.stopRemoteLoggingService();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            remoteLoggingService = null;
            receiverQueueProcessor = null;
            return true;
        }
        //throw new Exception("Logging zirk tried to stop that is not started");
        return false;
    }

    /**
     * Starts the logging Client
     *
     * @param remoteIP   Ip of the remote logging zirk
     * @param remotePort port of the remote logging zirk
     * @throws Exception if parameters are wrong or zirk is not available.
     */
    public void startLoggingClient(String remoteIP, int remotePort) throws Exception {
        if (logClient == null) {
            logClient = new LoggingClient();
            logClient.startClient(remoteIP, remotePort);
            return;
        }
        logClient.updateClient(remoteIP, remotePort);
    }

    /**
     * Stops the logging client
     *
     * @param remoteIP   IP of the remote zirk that is shutting
     * @param remotePort Port of the remote zirk that is shutting
     * @throws Exception if tried to stop the client that is not started
     */
    public void stopLoggingClient(String remoteIP, int remotePort) throws Exception {
        if (logClient != null) {
            logClient.stopClient(remoteIP, remotePort);
            logClient = null;
        }
    }
}
