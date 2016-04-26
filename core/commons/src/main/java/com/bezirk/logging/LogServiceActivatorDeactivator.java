/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.logging;

import com.bezirk.comms.IUhuComms;
import com.bezirk.comms.UhuComms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezrik.network.UhuNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used by the platform specific Logger Services to send the LoggingMessage
 * to the clients. This is a util class that just constructs and loads the message into the
 * Control Sender Queue.
 */

public final class LogServiceActivatorDeactivator {
    private static final Logger log = LoggerFactory.getLogger(LogServiceActivatorDeactivator.class);

    private LogServiceActivatorDeactivator() {
    }

    public static void sendLoggingServiceMsgToClients(IUhuComms comms, final String[] sphereList, final String[] selectedLogSpheres, final boolean isActivate) {
        final BezirkZirkId myId = new BezirkZirkId("UHU-REMOTE-LOGGING-SERVICE");
        final BezirkZirkEndPoint sep = UhuNetworkUtilities.getServiceEndPoint(myId);

        for (String sphereId : sphereList) {
            final ControlLedger controlLedger = new ControlLedger();
            final LoggingServiceMessage loggingServiceActivateRequest = new LoggingServiceMessage(sep, sphereId, UhuNetworkUtilities.getDeviceIp(), UhuComms.getREMOTE_LOGGING_PORT(), selectedLogSpheres, isActivate);
            controlLedger.setSphereId(sphereId);
            controlLedger.setMessage(loggingServiceActivateRequest);
            controlLedger.setSerializedMessage(controlLedger.getMessage().serialize());
            comms.sendMessage(controlLedger);

        }

    }

}
