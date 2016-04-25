/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 2/17/2015
 */
package com.bezirk.remotelogging;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes the LogReceiverQueue. It makes a blocking call on the Log Receiver Queue and
 * waits for the queue to be updated. It retrieve the String from the LogReceiverQueue and
 * converts (de-serializes) it into the UhuLoggingMessage and gives it to the platform specific
 * UhuLoggingHandler to update the UI.
 */
public class LogReceiverQueueProcessor extends Thread {
    /**
     * private logger for the class
     */
    private static final Logger logger = LoggerFactory.getLogger(LogReceiverQueueProcessor.class);
    /**
     * Platform specific logger
     */
    private final IUhuLogging platformSpecificLogger;
    /**
     * Flag used for starting/ Stopping Threads!
     */
    private boolean isRunning = false;
    /**
     * Gson to fromJson into UhuLoggingMessage
     */
    private Gson gson = null;

    /**
     * Setup the Processor.
     *
     * @param logger platform Specific Logger that is used to update the UI.
     */
    public LogReceiverQueueProcessor(IUhuLogging logger) {
        this.platformSpecificLogger = logger;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                StringBuilder logMsgString = LoggingQueueManager.fetchFromLogReceiverQueue();

                try {
                    UhuLoggingMessage logMsg = gson.fromJson(logMsgString.toString(), UhuLoggingMessage.class);
                    if (Util.LOGGING_VERSION.equals(logMsg.version)) {
                        platformSpecificLogger.handleLogMessage(logMsg);
                    } else {
                        logger.error("LOGGING VERSION MISMATCH!!" + "Received LOG MSG VERSION = " + logMsg.version +
                                " CURRENT LOGGING VERSION: " + Util.LOGGING_VERSION);
                    }
                } catch (Exception e) {
                    logger.error("Some error occured in LogReceiverQueueProcessor \n", e);
                }
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Starts Processing the LogReceiverQueue
     *
     * @throws Exception if platform specific logger is not set
     */
    public void startProcesing() throws Exception {
        if (null == platformSpecificLogger) {
            throw new Exception("IUhuLogger is not set");
        }
        gson = new Gson();
        isRunning = true;
        this.start();
    }

    /**
     * Stop processing the LogReceiverQueue.
     *
     * @throws Exception intruppted Exception if something goes down while stopping the thread.
     */
    public void stopProcessing() throws Exception {
        isRunning = false;
    }
}
