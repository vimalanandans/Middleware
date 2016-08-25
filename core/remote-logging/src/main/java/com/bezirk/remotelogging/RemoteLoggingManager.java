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
    /**
     * RemoteLoggingService
     */
    private RemoteLoggingService remoteLoggingService = null;
    /**
     * Logging Client
     */
    private LoggingClient logClient = null;

    private static final Logger logger = LoggerFactory.getLogger(RemoteLoggingManager.class);

    private final Date currentDate = new Date();

    private RemoteLoggingMessageNotification remoteLoggingMessageNotification = null;

    private  NetworkManager networkManager = null;

    private boolean enableLogging = false;

    ServiceMessageHandler logServiceMsgHandler = null;

    Comms comms;

    CommCtrlReceiver ctrlReceiver ;


    public RemoteLoggingManager(Comms comms, NetworkManager networkManager, RemoteLoggingMessageNotification remoteLoggingMessageNotification) {

        this.networkManager = networkManager;

        this.remoteLoggingMessageNotification = remoteLoggingMessageNotification;

        this.comms = comms;

        ctrlReceiver = new CommCtrlReceiver(this);

        comms.registerControlMessageReceiver(ControlMessage.Discriminator.LoggingServiceMessage,ctrlReceiver);
    }


    /**
     *
     * @param enable - True - enable, False - disable
     * @param sphereNameList - sphere name list. Null or RemoteLog.ALL_SPHERES means all sphere
     * @return
     */
    @Override
    public boolean enableLogging(boolean enable, String[] sphereNameList) {

        boolean bReturn ;

        this.enableLogging = enable;

        String[] loggingSpheres;

        if(enable)
        {
            bReturn = startRemoteLoggingService();
        }else
        {
            bReturn = stopRemoteLoggingService();
        }

        if(sphereNameList == null)
        {
            loggingSpheres = new String[1];
            loggingSpheres[0] = RemoteLog.ALL_SPHERES;
        }else if (RemoteLog.ALL_SPHERES.equals(sphereNameList)) {
            loggingSpheres = new String[1];
            loggingSpheres[0] = RemoteLog.ALL_SPHERES;
        } else {
            loggingSpheres = sphereNameList;
        }

        if(bReturn) {
            // Send the logging enable/disable to all the other nodes
            bReturn = bReturn & sendLoggingServiceMsgToClients(comms,
                    sphereNameList, loggingSpheres, enable);
        }


        return bReturn;
    }


    @Override
    public boolean isRemoteLoggingEnabled() {
        return enableLogging;
    }

    private boolean sendLoggingServiceMsgToClients(Comms comms, final String[] sphereList,
                    final String[] selectedLogSpheres, final boolean isActivate) {

        final ZirkId myId = new ZirkId("BEZIRK-REMOTE-LOGGING-SERVICE");

        final BezirkZirkEndPoint sep = new BezirkZirkEndPoint(comms.getNodeId(),myId);

        boolean sendStatus = false;


        for (String sphereId : sphereList) {

            final LoggingServiceMessage loggingServiceActivateRequest = new LoggingServiceMessage(sep,
                    sphereId, networkManager.getDeviceIp(), remoteLoggingService.getPort(), selectedLogSpheres, isActivate);

            if(null != sphereId &&
                    null != loggingServiceActivateRequest &&
                    null != loggingServiceActivateRequest.serialize()){
                sendStatus = true;
            }else{
                sendStatus = false;
                logger.error("unable to send the logging message to sphere id " + sphereId );
            }

            if(null != comms){
                comms.sendControlMessage(loggingServiceActivateRequest);
            }else{
                logger.debug("comms is null");
            }

        }
        return sendStatus;
    }

    @Override
    public boolean sendRemoteLogLedgerMessage(Ledger ledger) {

        logger.debug("sendRemoteLogLedgerMessage method in common/RemoteLoggingManager");

        boolean returnValue = false;

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
     * Starts the Logging service to cature the log messages
     */
    private boolean startRemoteLoggingService() {
        if (remoteLoggingService == null ) {
            remoteLoggingService = new RemoteLoggingService(remoteLoggingMessageNotification);
        }

        return  remoteLoggingService.startRemoteLoggingService();
    }

    /**
     * Stops the logging Zirk
     *
     */
    private boolean stopRemoteLoggingService() {
        if (remoteLoggingService != null) {
            try {
                remoteLoggingService.stopRemoteLoggingService();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            remoteLoggingService = null;

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
