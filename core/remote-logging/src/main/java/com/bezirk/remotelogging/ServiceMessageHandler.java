/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.remotelogging;

import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


/**
 * Handles the LogMessage received from the Log Zirk. It activates/Deactivates/Updates
 * the logging zirk properties for the client to logger the data.
 */
public final class ServiceMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServiceMessageHandler.class);
    /**
     * Logging Manager to start/ stop the logging client
     */
    private RemoteLoggingManager loggingManager = null;



    public static boolean checkLoggingServiceMessage(final LoggingServiceMessage logServiceMsg) {
        return !(null == logServiceMsg || !checkRemoteLoggingIPAndPort(logServiceMsg) || !checkSphereListIsEmpty(logServiceMsg.getSphereList()));
    }

    private static boolean checkRemoteLoggingIPAndPort(
            LoggingServiceMessage logServiceMsg) {

        return !(!ValidatorUtility.checkForString(logServiceMsg.getRemoteLoggingServiceIP()) ||
                logServiceMsg.getRemoteLoggingServicePort() == -1);
    }

    private static boolean checkSphereListIsEmpty(String[] sphereList) {
        return !(sphereList == null || sphereList.length == 0);
    }
    /**
     * Handles the LogServiceMessage.
     * It will start/ stop the logging client accordingly based on the status received on the LoggingServiceMessage
     *
     * @param loggingServiceMsg
     */
    public void handleLogServiceMessage(final LoggingServiceMessage loggingServiceMsg) {
        if (checkLoggingServiceMessage(loggingServiceMsg)) {
            if (loggingServiceMsg.isLoggingStatus()) {//Start or Update the client
                if (null == loggingManager) {
                    loggingManager = new RemoteLoggingManager();
                }
                try {
                    loggingManager.startLoggingClient(loggingServiceMsg.getRemoteLoggingServiceIP(), loggingServiceMsg.getRemoteLoggingServicePort());
                } catch (Exception e) {
                    logger.error("Error occurred while logging client", e);
                }
                FilterLogMessages.setLoggingSphereList(Arrays.asList(loggingServiceMsg.getSphereList()));
                LoggingStatus.setLoggingEnabled(loggingServiceMsg.isLoggingStatus());
            } else {
                if (ValidatorUtility.isObjectNotNull(loggingManager)) {
                    try {
                        loggingManager.stopLoggingClient(loggingServiceMsg.getRemoteLoggingServiceIP(), loggingServiceMsg.getRemoteLoggingServicePort());
                        loggingManager = null;
                        LoggingStatus.setLoggingEnabled(loggingServiceMsg.isLoggingStatus());
                    } catch (Exception e) {
                        logger.error("Error occurred while stopping client", e);
                    }

                } else {
                    logger.debug("Tried to stop the logging client that's not started");
                }
            }
            //LoggingStatus.setLoggingEnabled(loggingServiceMsg.isLoggingStatus());
        } else {
            logger.error("Logging Zirk Message failed Validation");
        }
    }


}