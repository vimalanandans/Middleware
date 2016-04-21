/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.logging;

import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.remotelogging.manager.UhuLoggingManager;
import com.bezirk.remotelogging.spherefilter.FilterLogMessages;
import com.bezirk.remotelogging.status.LoggingStatus;
import com.bezirk.util.UhuValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


/**
 * Handles the LogMessage received from the Log Service. It activates/Deactivates/Updates
 * the logging service properties for the client to logger the data.
 */
public final class LogServiceMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(LogServiceMessageHandler.class);
    /**
     * Logging Manager to start/ stop the logging client
     */
    private UhuLoggingManager loggingManager = null;

    /**
     * Handles the LogServiceMessage.
     * It will start/ stop the logging client accordingly based on the status received on the LoggingServiceMessage
     *
     * @param loggingServiceMsg
     */
    public void handleLogServiceMessage(final LoggingServiceMessage loggingServiceMsg) {
        if (UhuValidatorUtility.checkLoggingServiceMessage(loggingServiceMsg)) {
            if (loggingServiceMsg.isLoggingStatus()) {//Start or Update the client
                if (null == loggingManager) {
                    loggingManager = new UhuLoggingManager();
                }
                try {
                    loggingManager.startLoggingClient(loggingServiceMsg.getRemoteLoggingServiceIP(), loggingServiceMsg.getRemoteLoggingServicePort());
                } catch (Exception e) {
                    logger.error("Error occured while logging client", e);
                }
                FilterLogMessages.setLoggingSphereList(Arrays.asList(loggingServiceMsg.getSphereList()));
                LoggingStatus.setLoggingEnabled(loggingServiceMsg.isLoggingStatus());
            } else {
                if (UhuValidatorUtility.isObjectNotNull(loggingManager)) {
                    try {
                        loggingManager.stopLoggingClient(loggingServiceMsg.getRemoteLoggingServiceIP(), loggingServiceMsg.getRemoteLoggingServicePort());
                        loggingManager = null;
                        LoggingStatus.setLoggingEnabled(loggingServiceMsg.isLoggingStatus());
                    } catch (Exception e) {
                        logger.error("Error occured while stopping client", e);
                    }

                } else {
                    logger.debug("Tried to stop the logging client that's not started");
                }
            }
            //LoggingStatus.setLoggingEnabled(loggingServiceMsg.isLoggingStatus());
        } else {
            logger.error("Logging Service Message failed Validation");
        }
    }


}
