/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.remotelogging;

import com.bezirk.comms.CommsConfigurations;
import com.bezirk.comms.BezirkComms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezrik.network.BezirkNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used by the platform specific Logger Services to send the LoggingMessage
 * to the clients. This is a util class that just constructs and loads the message into the
 * Control Sender Queue.
 */

public final class LogServiceActivatorDeactivator {
    private static final Logger logger = LoggerFactory.getLogger(LogServiceActivatorDeactivator.class);

    private LogServiceActivatorDeactivator() {
    }

    public static void sendLoggingServiceMsgToClients(BezirkComms comms, final String[] sphereList, final String[] selectedLogSpheres, final boolean isActivate) {
        final ZirkId myId = new ZirkId("BEZIRK-REMOTE-LOGGING-SERVICE");
        final BezirkZirkEndPoint sep = BezirkNetworkUtilities.getServiceEndPoint(myId);

        for (String sphereId : sphereList) {
            final ControlLedger controlLedger = new ControlLedger();
            final LoggingServiceMessage loggingServiceActivateRequest = new LoggingServiceMessage(sep, sphereId, BezirkNetworkUtilities.getDeviceIp(), CommsConfigurations.getREMOTE_LOGGING_PORT(), selectedLogSpheres, isActivate);
            controlLedger.setSphereId(sphereId);
            controlLedger.setMessage(loggingServiceActivateRequest);
            controlLedger.setSerializedMessage(controlLedger.getMessage().serialize());
            comms.sendMessage(controlLedger);

        }

    }

}
