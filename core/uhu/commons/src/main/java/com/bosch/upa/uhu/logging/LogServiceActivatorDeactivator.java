/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bosch.upa.uhu.logging;

import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.comms.UhuComms;
import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.control.messages.logging.LoggingServiceMessage;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used by the platform specific Logger Services to send the LoggingMessage 
 * to the clients. This is a util class that just constructs and loads the message into the 
 * Control Sender Queue.
 */

public final class LogServiceActivatorDeactivator {
    private static final Logger log = LoggerFactory.getLogger(LogServiceActivatorDeactivator.class);
	private LogServiceActivatorDeactivator() {}
	
	public static void sendLoggingServiceMsgToClients(IUhuComms comms, final String[] sphereList, final String[] selectedLogSpheres, final boolean isActivate){
		final UhuServiceId myId = new UhuServiceId("UHU-REMOTE-LOGGING-SERVICE");
		final UhuServiceEndPoint sep = UhuNetworkUtilities.getServiceEndPoint(myId);
		
		for(String sphereId: sphereList){
			final ControlLedger controlLedger = new ControlLedger();
			final LoggingServiceMessage loggingServiceActivateRequest = new LoggingServiceMessage(sep, sphereId, UhuNetworkUtilities.getDeviceIp(), UhuComms.getREMOTE_LOGGING_PORT(), selectedLogSpheres, isActivate);
			controlLedger.setSphereId(sphereId);
			controlLedger.setMessage(loggingServiceActivateRequest);
			controlLedger.setSerializedMessage(controlLedger.getMessage().serialize());
            comms.sendMessage(controlLedger);

		}
		
	}
	
}
