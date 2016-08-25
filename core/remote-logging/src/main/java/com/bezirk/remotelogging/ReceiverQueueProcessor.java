package com.bezirk.remotelogging;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

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
     * Blocking Queue that is used to queue logger messages at the Logging Zirk.
     *
     * @see RemoteLoggingService
     */
    private BlockingQueue<String> logReceiverQueue = null;


    /**
     * Setup the Processor.
     *
     * @param logger platform Specific Logger that is used to update the UI.
     */
    public ReceiverQueueProcessor(RemoteLoggingMessageNotification logger) {
        this.platformSpecificLogger = logger;
        gson = new Gson();
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                StringBuilder logMsgString = getLogIncomingMessage();

                try {
                    RemoteLoggingMessage logMsg = gson.fromJson(logMsgString.toString(), RemoteLoggingMessage.class);
                    if (Util.LOGGING_VERSION.equals(logMsg.version)) {
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
     */
    public boolean startProcessing() {
        logger.debug("startProcessing of Receiverqueueprocessor");

        if(isRunning == false) {
            Thread t = new Thread(this);
            t.start();
        }
        isRunning = true;
        return isRunning;
    }

    /**
     * Stop processing the LogReceiverQueue.
     *
     * @throws Exception interrupted Exception if something goes down while stopping the thread.
     */
    public void stopProcessing() throws Exception {
        isRunning = false;
    }




    /**
     * loads the serialized RemoteLogMessage into LogReceiverQueue
     *
     * @param serializedLogMsg serialized Log Message
     * @throws InterruptedException if multiple threads try to access the queue.
     */
    void processLogInMessage(String serializedLogMsg) throws InterruptedException {
        if (logReceiverQueue == null) {
            logReceiverQueue = new SynchronousQueue<String>();
        }
        logReceiverQueue.put(serializedLogMsg);
    }

    /**
     * Waits on the logReceiverQueue to retrieve the logger Message
     *
     * @return String representation of the RemoteLogMessage
     * @throws InterruptedException if multiple threads try to access the queue.
     */
    StringBuilder getLogIncomingMessage() throws InterruptedException {
        if (logReceiverQueue == null) {
            logReceiverQueue = new SynchronousQueue<String>();
        }
        return new StringBuilder(logReceiverQueue.take());
    }


}
