package com.bezirk.remotelogging;


import com.bezirk.comms.Comms;
import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.logging.LoggingServiceMessage;

import com.bezirk.networking.NetworkManager;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Logging Manager class that starts/stops LoggingServices and LoggingClient.
 * The platforms need to instantiate this manager and can start stop the zirk.
 */
public  class RemoteLoggingManager implements RemoteLog {

    public static int REMOTE_LOGGING_PORT = 7777;

    private boolean remoteLoggingForAllSpheres = false;

    public boolean isRemoteLoggingForAllSpheres() {
        return remoteLoggingForAllSpheres;
    }

    public void setRemoteLoggingForAllSpheres(boolean remoteLoggingForAllSpheres) {
        this.remoteLoggingForAllSpheres = remoteLoggingForAllSpheres;
    }
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

    private RemoteLoggingMessageNotification remoteLoggingMessageNotification = null;

    private  NetworkManager networkManager = null;

    private boolean sendLoggingeMsgToClients = false;

    ServiceMessageHandler logServiceMsgHandler = null;

    Comms comms;

    CommCtrlReceiver ctrlReceiver ;


    public RemoteLoggingManager(NetworkManager networkManager, RemoteLoggingMessageNotification remoteLoggingMessageNotification) {
        this.networkManager = networkManager;
        this.remoteLoggingMessageNotification = remoteLoggingMessageNotification;
        CommCtrlReceiver ctrlReceiver = new CommCtrlReceiver(this);
    }


    @Override
    public boolean enableLogging(boolean enable, String[] sphereNameList) {
        logger.debug("enable logging in remotelogging");

        String[] loggingSpheres;

        if (RemoteLog.ALL_SPHERES.equals(sphereNameList)) {
            loggingSpheres = new String[1];
            loggingSpheres[0] = RemoteLog.ALL_SPHERES;
        } else {
            loggingSpheres = sphereNameList;
        }

        // Send the logging enable to all the other nodes
        sendLoggingeMsgToClients = sendLoggingServiceMsgToClients(comms,
                sphereNameList, loggingSpheres, enable);

        return sendLoggingeMsgToClients;
    }

    @Override
    public boolean enableRemoteLoggingForAllSpheres()
    {
     boolean remoteLogValue = isRemoteLoggingForAllSpheres();
        logger.debug("remoteLogValue is "+remoteLogValue);
        return  remoteLogValue;
    }

    @Override
    public boolean isRemoteLoggingEnabled() {
        return sendLoggingeMsgToClients;
    }
    public boolean sendLoggingServiceMsgToClients(Comms comms, final String[] sphereList,
                    final String[] selectedLogSpheres, final boolean isActivate) {

        final ZirkId myId = new ZirkId("BEZIRK-REMOTE-LOGGING-SERVICE");

        final BezirkZirkEndPoint sep = new BezirkZirkEndPoint(comms.getNodeId(),myId);

        boolean sendLogMessageToClient = false;


        for (String sphereId : sphereList) {

            final LoggingServiceMessage loggingServiceActivateRequest = new LoggingServiceMessage(sep,
                    sphereId, comms.getNodeId(), REMOTE_LOGGING_PORT, selectedLogSpheres, isActivate);

            if(null != sphereId &&
                    null != loggingServiceActivateRequest &&
                    null != loggingServiceActivateRequest.serialize()){
                sendLogMessageToClient = true;
            }else{
                sendLogMessageToClient = false;
                logger.error("unable to send the logging message to sphere id " + sphereId );
            }

            if(null != comms){
                comms.sendControlMessage(loggingServiceActivateRequest);
            }else{
                logger.debug("comms is null");
            }

        }
        return sendLogMessageToClient;
    }

    @Override
    public boolean sendRemoteLogLedgerMessage(Ledger ledger) {

        logger.debug("sendRemoteLogLedgerMessage method in common/RemoteLoggingManager");

        boolean returnValue = false;

        boolean isEnabled =  isRemoteLoggingEnabled();

        logger.debug("checkEnableForAllSphere in PubSubBroker is " + isEnabled);

        RemoteLoggingMessage remoteLoggingMessage = null;

        if(ledger instanceof EventLedger){
                if((null!=((EventLedger) ledger).getHeader())){
                    logger.debug("Header is set "+((EventLedger) ledger).getHeader().toString());
                    if(null!=((EventLedger) ledger).getHeader().getSphereId()){
                        logger.debug("sphere id is "+((EventLedger) ledger).getHeader().getSphereId());
                    }
                }
                /*logger.debug("getSphereId is "+((EventLedger) ledger).getHeader().getSphereId());
                logger.debug("String.valueOf(currentDate.getTime() is "+String.valueOf(currentDate.getTime()));
               // logger.debug(" device.getDeviceName() is "+ device.getDeviceName());
                logger.debug("Util.CONTROL_RECEIVER_VALUE is "+Util.CONTROL_RECEIVER_VALUE);
                logger.debug("((EventLedger) ledger).getHeader().getUniqueMsgId() is "+((EventLedger) ledger).getHeader().getUniqueMsgId());
                logger.debug("Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_RECEIVE.name() is "+Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_RECEIVE.name());
                logger.debug("Util.LOGGING_VERSION).serialize() is "+Util.LOGGING_VERSION);*/

            remoteLoggingMessage = new RemoteLoggingMessage(((EventLedger) ledger).getHeader().getSphereId(),
                    String.valueOf(currentDate.getTime()),
                    Util.CONTROL_RECEIVER_VALUE, ((EventLedger) ledger).getHeader().getUniqueMsgId(),
                    Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_RECEIVE.name(), Util.LOGGING_VERSION);

            try {

                LoggingQueueManager.loadLogSenderQueue(remoteLoggingMessage.serialize());

                returnValue = true;

            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }else if(ledger instanceof ControlLedger){
            if(FilterLogMessages.checkSphere(((ControlLedger) ledger).getSphereId()))
            {
                remoteLoggingMessage = new RemoteLoggingMessage(
                        ((ControlLedger) ledger).getSphereId(),
                        String.valueOf(currentDate.getTime()),
                        Util.CONTROL_RECEIVER_VALUE,
                        ((ControlLedger) ledger).getMessage().getUniqueKey(),
                        Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name(),
                        Util.LOGGING_VERSION);
                try {
                    LoggingQueueManager.loadLogSenderQueue(remoteLoggingMessage.serialize());
                    returnValue = true;
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        if(returnValue && remoteLoggingMessage != null && remoteLoggingMessageNotification != null)
        {
            remoteLoggingMessageNotification.handleLogMessage(remoteLoggingMessage);
        }

        return returnValue;
    }



    @Override
    public boolean sendRemoteLogControlMessage(ControlMessage msg) {
        if (FilterLogMessages.checkSphere(msg.getSphereId())) {
            try {
                LoggingQueueManager.loadLogSenderQueue(
                        new RemoteLoggingMessage(
                                msg.getSphereId(),
                                String.valueOf(currentDate.getTime()),
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


    class CommCtrlReceiver implements CtrlMsgReceiver {

        RemoteLoggingManager loggingManager = null;

        public CommCtrlReceiver(RemoteLoggingManager loggingManager)
        {
            this.loggingManager = loggingManager;
        }
        @Override
        // FIXME : remove the below Log related quickfix, by moving the implementation to respective module
        public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
            switch (id) {
                case LoggingServiceMessage:

                    logger.debug("ReceivedLogMessage-> " + serializedMsg);
                    try {
                        final LoggingServiceMessage loggingServiceMsg = ControlMessage.deserialize(serializedMsg, LoggingServiceMessage.class);

                        if (null == logServiceMsgHandler) {
                            logServiceMsgHandler = new ServiceMessageHandler(loggingManager);
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
    public boolean startRemoteLoggingService(final RemoteLoggingMessageNotification platformSpecificHandler) {
        if (remoteLoggingService == null && platformSpecificHandler != null) {
            remoteLoggingService = new RemoteLoggingService(REMOTE_LOGGING_PORT);
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
