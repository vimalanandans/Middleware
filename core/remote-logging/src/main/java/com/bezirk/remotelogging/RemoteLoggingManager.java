package com.bezirk.remotelogging;


import com.bezirk.comms.Comms;
import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.logging.LoggingServiceMessage;

import com.bezirk.networking.NetworkManager;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Logging Manager class that starts/stops LoggingServices and RemoteLoggingClient.
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
    private RemoteLoggingClient remoteLoggingClient = null;



    private static final Logger logger = LoggerFactory.getLogger(RemoteLoggingManager.class);



    private RemoteLoggingMessageNotification remoteLoggingMessageNotification = null;

    private  NetworkManager networkManager = null;

    private boolean enableLogging = false;

    private boolean enableControl = false;


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
    public boolean enableLogging(boolean enable, boolean enableControl, String[] sphereNameList) {

        boolean bReturn ;

        this.enableLogging = enable;

        this.enableControl = enableControl;

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
    public boolean sendRemoteLogToServer(Ledger ledger) {

        logger.debug("sendRemoteLogLedgerMessage method in common/RemoteLoggingManager");

        return remoteLoggingClient.processLogInMessage(ledger);
    }
    @Override
    public boolean sendRemoteLogToServer(ControlMessage message)
    {
        return remoteLoggingClient.processLogInMessage(message);
    }

    /** to recieve the logging request*/
    class CommCtrlReceiver implements CtrlMsgReceiver {

        RemoteLoggingManager loggingManager = null;

        public CommCtrlReceiver(RemoteLoggingManager loggingManager)
        {
            this.loggingManager = loggingManager;
        }
        @Override
        public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
            switch (id) {
            case LoggingServiceMessage:

                logger.debug("ReceivedLogMessage-> " + serializedMsg);

                final LoggingServiceMessage loggingServiceMsg = ControlMessage.deserialize(serializedMsg, LoggingServiceMessage.class);

                handleLogRequest(loggingServiceMsg);

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
     * Handles the LogServiceMessage.
     * It will start/ stop the logging client accordingly based on the status received on the LoggingServiceMessage
     *
     * @param loggingServiceMsg
     */
    public void handleLogRequest(final LoggingServiceMessage loggingServiceMsg) {

        if (loggingServiceMsg.isLoggingStatus()) {
            //Start or Update the client
            try {
                startLoggingClient(loggingServiceMsg.getRemoteLoggingServiceIP(),
                        loggingServiceMsg.getRemoteLoggingServicePort());
            } catch (Exception e) {
                logger.error("Error occurred while logging client", e);
            }
            Util.setLoggingSphereList(Arrays.asList(loggingServiceMsg.getSphereList()));
        } else {

            try {
                stopLoggingClient(loggingServiceMsg.getRemoteLoggingServiceIP(),
                        loggingServiceMsg.getRemoteLoggingServicePort());
            } catch (Exception e) {
                logger.error("Error occurred while stopping client", e);
            }
        }

    }

    /**
     * Starts the logging Client
     *
     * @param remoteIP   Ip of the remote logging zirk
     * @param remotePort port of the remote logging zirk
     * @throws Exception if parameters are wrong or zirk is not available.
     */
    public boolean startLoggingClient(String remoteIP, int remotePort) throws Exception {

        if(ValidatorUtility.checkForString(remoteIP) || remotePort == 0 ){
            logger.debug("invalid remote ip :" + remoteIP + " or remoteport " + remotePort);
            return false;
        }

        if (remoteLoggingClient == null) {
            remoteLoggingClient = new RemoteLoggingClient(comms.getNodeId());
            return remoteLoggingClient.startClient(remoteIP, remotePort);
        } // incase if already running
        return remoteLoggingClient.updateClient(remoteIP, remotePort);
    }

    /**
     * Stops the logging client
     *
     * @param remoteIP   IP of the remote zirk that is shutting
     * @param remotePort Port of the remote zirk that is shutting
     * @throws Exception if tried to stop the client that is not started
     */
    public boolean  stopLoggingClient(String remoteIP, int remotePort) throws Exception {
        if(ValidatorUtility.checkForString(remoteIP) || remotePort == 0 ){
            logger.debug("invalid remote ip :" + remoteIP + " or remoteport " + remotePort);
            return false;
        }
        if (remoteLoggingClient != null) {
            remoteLoggingClient.stopClient(remoteIP, remotePort);
            remoteLoggingClient = null;
        }
        return true;
    }
}
