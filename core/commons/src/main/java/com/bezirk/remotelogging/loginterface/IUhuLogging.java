/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 2/17/2015
 */
package com.bezirk.remotelogging.loginterface;

import com.bezirk.remotelogging.messages.UhuLoggingMessage;

/**
 * Generic Interface that each of the platforms should implement to receive the log Messages.
 * The Platforms can process the log message and handle it in UI.
 */
public interface IUhuLogging {
    /**
     * Callback given by the Uhu-Logging module to the platform specific logging.
     *
     * @param uhuLogMessage UhuLoggingMessage containing the Logging information.
     */
    public void handleLogMessage(UhuLoggingMessage uhuLogMessage);
}
