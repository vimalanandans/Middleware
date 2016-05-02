package com.bezirk.remotelogging.processors;

import com.bezirk.remotelogging.loginterface.BezirkLogging;
import com.bezirk.remotelogging.messages.BezirkLoggingMessage;
import com.bezirk.remotelogging.queues.LoggingQueueManager;
import com.bezirk.remotelogging.util.Util;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes the LogReceiverQueue. It makes a blocking call on the Log Receiver Queue and
 * waits for the queue to be updated. It retrieve the String from the LogReceiverQueue and
 * converts (de-serializes) it into the BezirkLoggingMessage and gives it to the platform specific
 * BezirkLoggingHandler to update the UI.
 */
public class LogReceiverQueueProcessor extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(LogReceiverQueueProcessor.class);
    /**
     * Platform specific logger
     */
    private final BezirkLogging platformSpecificLogger;
    /**
     * Flag used for starting/ Stopping Threads!
     */
    private boolean isRunning = false;
    /**
     * Gson to fromJson into BezirkLoggingMessage
     */
    private Gson gson = null;

    /**
     * Setup the Processor.
     *
     * @param logger platform Specific Logger that is used to update the UI.
     */
    public LogReceiverQueueProcessor(BezirkLogging logger) {
        this.platformSpecificLogger = logger;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                StringBuilder logMsgString = LoggingQueueManager.fetchFromLogReceiverQueue();

                try {
                    BezirkLoggingMessage logMsg = gson.fromJson(logMsgString.toString(), BezirkLoggingMessage.class);
                    if (Util.LOGGING_VERSION.equals(logMsg.version)) {
                        platformSpecificLogger.handleLogMessage(logMsg);
                    } else {
                        logger.error("LOGGING VERSION MISMATCH!!" + "Received LOG MSG VERSION = " + logMsg.version +
                                " CURRENT LOGGING VERSION: " + Util.LOGGING_VERSION);
                    }
                } catch (Exception e) {
                    logger.error("Some error occurred in LogReceiverQueueProcessor \n", e);
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
    public void startProcessing() throws Exception {
        if (null == platformSpecificLogger) {
            throw new Exception("BezirkLogger is not set");
        }
        gson = new Gson();
        isRunning = true;
        this.start();
    }

    /**
     * Stop processing the LogReceiverQueue.
     *
     * @throws Exception interrupted Exception if something goes down while stopping the thread.
     */
    public void stopProcessing() throws Exception {
        isRunning = false;
    }
}
