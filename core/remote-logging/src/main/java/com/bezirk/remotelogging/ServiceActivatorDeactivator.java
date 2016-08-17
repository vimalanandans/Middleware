/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.remotelogging;

import com.bezirk.comms.Comms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.networking.NetworkManager;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used by the platform specific Logger Services to send the LoggingMessage
 * to the clients. This is a util class that just constructs and loads the message into the
 * Control Sender Queue.
 */

public final class ServiceActivatorDeactivator {
    private static final Logger logger = LoggerFactory.getLogger(ServiceActivatorDeactivator.class);

    // Move to the remote logging component manager
    public static int REMOTE_LOGGING_PORT = 7777;

    public static boolean sendLoggingServiceMsgToClients(Comms comms, final String[] sphereList, final String[] selectedLogSpheres, final boolean isActivate, final NetworkManager networkManager) {
        final ZirkId myId = new ZirkId("BEZIRK-REMOTE-LOGGING-SERVICE");
        logger.debug("inside serviceactivator of remotelogging ");
        final BezirkZirkEndPoint sep = networkManager.getServiceEndPoint(myId);
        boolean sendLogMessageToClient=false;
        for (String sphereId : sphereList) {
            final ControlLedger controlLedger = new ControlLedger();
            final LoggingServiceMessage loggingServiceActivateRequest = new LoggingServiceMessage(sep, sphereId, networkManager.getDeviceIp(), REMOTE_LOGGING_PORT, selectedLogSpheres, isActivate);
            controlLedger.setSphereId(sphereId);
            controlLedger.setMessage(loggingServiceActivateRequest);
            controlLedger.setSerializedMessage(controlLedger.getMessage().serialize());
            if(null!=sphereId && null!=loggingServiceActivateRequest && null!=controlLedger.getMessage().serialize()){
                sendLogMessageToClient=true;
            }else{
                sendLogMessageToClient=false;
            }
            logger.debug("sendLogMessageToClient value is "+sendLogMessageToClient);
            if(null!=comms){
                logger.debug("comms is not null");
                comms.sendControlLedger(controlLedger);
                //comms.sendMessage(controlLedger);
            }else{
                logger.debug("comms is null");
            }


        }
        return sendLogMessageToClient;
    }

}
