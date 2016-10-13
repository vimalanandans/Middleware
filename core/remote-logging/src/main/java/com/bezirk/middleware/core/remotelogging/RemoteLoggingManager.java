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
package com.bezirk.middleware.core.remotelogging;



import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.comms.CtrlMsgReceiver;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.Ledger;
import com.bezirk.middleware.core.control.messages.logging.LoggingServiceMessage;
import com.bezirk.middleware.core.networking.NetworkManager;
import com.bezirk.middleware.core.pubsubbroker.PubSubBroker;
import com.bezirk.middleware.core.util.ValidatorUtility;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Logging Manager class that starts/stops LoggingServices and RemoteLoggingClient.
 * The platforms need to instantiate this manager and can start stop the zirk.
 */
public  class RemoteLoggingManager implements RemoteLog {
    private static final Logger logger = LoggerFactory.getLogger(RemoteLoggingManager.class);

    private RemoteLoggingServer remoteLoggingServer = null;
    private RemoteLoggingClient remoteLoggingClient = null;
    private RemoteLoggingMessageNotification remoteLoggingMessageNotification = null;

    private NetworkManager networkManager = null;

    private boolean enableLogging = false;
    private boolean enableControl = false;
    private boolean enableFileLogging = false;

    private final Comms comms;

    public RemoteLoggingManager(Comms comms, NetworkManager networkManager,
                                RemoteLoggingMessageNotification remoteLoggingMessageNotification) {
        this.networkManager = networkManager;
        this.remoteLoggingMessageNotification = remoteLoggingMessageNotification;
        this.comms = comms;
        remoteLoggingClient = new RemoteLoggingClient(networkManager);

        comms.registerControlMessageReceiver(ControlMessage.Discriminator.LOGGING_SERVICE_MESSAGE,
                new LogCtrlMessageReceiver(this));
    }

    /**
     *
     * @param enable - True - enable, False - disable
     * @param enableControl - True enable logging control messages
     * @param enableFileLogging - enable control messages
     * @param sphereNameList - sphere name list. Null or RemoteLog.ALL_SPHERES means all sphere
     * @return
     */
    @Override
    public boolean enableLogging(boolean enable, boolean enableControl, boolean enableFileLogging,
                                 String[] sphereNameList) {

        boolean bReturn ;

        this.enableLogging = enable;
        this.enableControl = enableControl;
        this.enableFileLogging = enableFileLogging;

        final String[] loggingSpheres;

        if(enable) {
            bReturn = startRemoteLoggingService();
        } else {
            bReturn = stopRemoteLoggingService();
        }

        if(sphereNameList == null) {
            loggingSpheres = new String[1];
            loggingSpheres[0] = RemoteLog.ALL_SPHERES;
            // this sphere list goes to comms. Rather than access the global
            String [] spheres = new String[1];
            spheres[0] = PubSubBroker.SPHERE_NULL_NAME;
            sphereNameList = spheres;
        } else if (RemoteLog.ALL_SPHERES.equals(sphereNameList)) {
            loggingSpheres = new String[1];
            loggingSpheres[0] = RemoteLog.ALL_SPHERES;
            // this sphere list goes to comms. Rather than access the global
            String [] spheres = new String[1];
            spheres[0] = PubSubBroker.SPHERE_NULL_NAME;
            sphereNameList = spheres;
        } else {
            loggingSpheres = sphereNameList;
        }

        return bReturn &&
                sendLoggingServiceMsgToClients(comms, sphereNameList, loggingSpheres, enable);
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
            final LoggingServiceMessage loggingServiceActivateRequest =
                    new LoggingServiceMessage(sep, sphereId, networkManager.getDeviceIp(),
                            remoteLoggingServer.getPort(), selectedLogSpheres, isActivate);

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

        return remoteLoggingClient.processLogInMessage(ledger);
    }
    @Override
    public boolean sendRemoteLogToServer(ControlMessage message)
    {
        return remoteLoggingClient.processLogInMessage(message);
    }

    /** to receive the logging request*/
    private class LogCtrlMessageReceiver implements CtrlMsgReceiver {
        private final RemoteLoggingManager loggingManager;

        public LogCtrlMessageReceiver(RemoteLoggingManager loggingManager) {
            this.loggingManager = loggingManager;
        }

        @Override
        public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
            switch (id) {
            case LOGGING_SERVICE_MESSAGE:

                logger.debug("ReceivedLogMessage-> " + serializedMsg);

                final LoggingServiceMessage loggingServiceMsg =
                        ControlMessage.deserialize(serializedMsg, LoggingServiceMessage.class);

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
     * Starts the Logging service to capture the log messages
     */
    private boolean startRemoteLoggingService() {
        if (remoteLoggingServer == null ) {
            remoteLoggingServer = new RemoteLoggingServer(remoteLoggingMessageNotification,
                    enableFileLogging);
        }
        return  remoteLoggingServer.startRemoteLoggingService();
    }

    /**
     * Stops the logging Zirk
     *
     */
    private boolean stopRemoteLoggingService() {
        if (remoteLoggingServer != null) {
            try {
                remoteLoggingServer.stopRemoteLoggingService();
            } catch (Exception e) {
                logger.error("Failed to stop remote logging server", e);
                return false;
            }
            remoteLoggingServer = null;

            return true;
        }
        //throw new Exception("Logging zirk tried to stop that is not started");
        return false;
    }

    /**
     * Handles the LogServiceMessage.
     * It will start/ stop the logging client accordingly based on the status received on the
     * LOGGING_SERVICE_MESSAGE
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
            logger.debug("invalid remote ip {} or remoteport {}", remoteIP, remotePort);
            return false;
        }

        if(!remoteLoggingClient.isRunning()){
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
            logger.debug("invalid remote ip {} or remoteport {}", remoteIP, remotePort);
            return false;
        }

        remoteLoggingClient.stopClient(remoteIP, remotePort);
        return true;
    }
}
