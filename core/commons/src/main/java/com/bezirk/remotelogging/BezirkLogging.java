package com.bezirk.remotelogging;

/**
 * Generic Interface that each of the platforms should implement to receive the logger Messages.
 * The Platforms can process the logger message and handle it in UI.
 */
public interface BezirkLogging {
    /**
     * Callback given by the Bezirk-Logging module to the platform specific logging.
     *
     * @param bezirkLogMessage BezirkLoggingMessage containing the Logging information.
     */
    public void handleLogMessage(BezirkLoggingMessage bezirkLogMessage);
}