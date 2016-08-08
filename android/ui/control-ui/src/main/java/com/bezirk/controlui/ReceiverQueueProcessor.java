package com.bezirk.controlui;

import com.bezirk.remotelogging.RemoteLoggingMessage;
import com.bezirk.remotelogging.RemoteLoggingMessageNotification;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes the LogReceiverQueue. It makes a blocking call on the Log Receiver Queue and
 * waits for the queue to be updated. It retrieve the String from the LogReceiverQueue and
 * converts (de-serializes) it into the RemoteLogMessage and gives it to the platform specific
 * BezirkLoggingHandler to update the UI.
 */
public class ReceiverQueueProcessor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ReceiverQueueProcessor.class);
    /**
     * Platform specific logger
     */
    private final RemoteLoggingMessageNotification platformSpecificLogger;
    /**
     * Flag used for starting/ Stopping Threads!
     */
    private boolean isRunning = false;
    /**
     * Gson to fromJson into RemoteLogMessage
     */
    private Gson gson = null;

    /**
     * Setup the Processor.
     *
     * @param logger platform Specific Logger that is used to update the UI.
     */
    public ReceiverQueueProcessor(RemoteLoggingMessageNotification logger) {
        this.platformSpecificLogger = logger;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                StringBuilder logMsgString = LoggingQueueManager.fetchFromLogReceiverQueue();
                logger.debug("logMsgString is "+logMsgString);
                try {
                    RemoteLoggingMessage logMsg = gson.fromJson(logMsgString.toString(), RemoteLoggingMessage.class);
                    //call back on the basis of checking the logging version
                    logger.debug("Util.LOGGING_VERSION is "+Util.LOGGING_VERSION);
                    logger.debug("logmsg version is "+logMsg.version);
                    if (Util.LOGGING_VERSION.equals(logMsg.version)) {
                        logger.debug("Versions are equal");
                        platformSpecificLogger.handleLogMessage(logMsg);
                    } else {
                        logger.error("LOGGING VERSION MISMATCH!!" + "Received LOG MSG VERSION = " + logMsg.version +
                                " CURRENT LOGGING VERSION: " + Util.LOGGING_VERSION);
                    }
                } catch (Exception e) {
                    logger.error("Some error occurred in ReceiverQueueProcessor \n", e);
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
        ReceiverQueueProcessor receiverQueueProcessor = new ReceiverQueueProcessor(platformSpecificLogger);
        Thread t = new Thread(receiverQueueProcessor);
        t.start();
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
