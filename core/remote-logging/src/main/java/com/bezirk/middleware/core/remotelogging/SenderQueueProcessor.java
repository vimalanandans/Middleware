package com.bezirk.middleware.core.remotelogging;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Processes the LogSenderQueue. It makes a blocking call on the Log sender Queue and waits for the queue to be updated.
 * It retrieve the String from the LogSenderQueue and pushes the message onto the Logging Zirk
 * and removes it from the queue.
 */
public class SenderQueueProcessor extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(SenderQueueProcessor.class);
    /**
     * Logging Zirk IP. This will set based on the LoggingMessage from the Logging zirk.
     */
    private final String remoteServiceIP;
    /**
     * Logging Zirk Port. This will set based on the LoggingMessage from the Logging zirk.
     */
    private final int remoteServicePort;
    /**
     * Flag used for starting/ Stopping Threads!
     */
    private boolean isRunning = false;

    /**
     * Setup the Logging Zirk Parameters
     *
     * @param remoteServiceIP IP address of the Logging Zirk
     * @param remotePort      Logging Zirk Port
     */
    public SenderQueueProcessor(String remoteServiceIP, int remotePort) {
        this.remoteServiceIP = remoteServiceIP;
        this.remoteServicePort = remotePort;
    }

    @Override
    public void run() {
        while (isRunning) {

            try {
                StringBuilder logMsgString = LoggingQueueManager.fetchFromLogSenderQueue();
                Socket bezirkClient = null;
                try {
                    bezirkClient = new Socket(remoteServiceIP, remoteServicePort);
                    DataOutputStream clientOutputStream = new DataOutputStream(bezirkClient.getOutputStream());
                    clientOutputStream.writeBytes(logMsgString.toString());
                    clientOutputStream.flush();
                    clientOutputStream.close();
                } catch (IOException e) {
                    logger.error("Some Error occurred :", e);
                } finally {
                    try {
                        if (bezirkClient != null) {
                            bezirkClient.close();
                        }
                    } catch (IOException e) {
                        logger.error("Errors occurred in closing bezirkClient \n", e);
                    }
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * Starts Processing the Log Sender Queue
     *
     * @throws Exception if Logging Zirk is down and unable to connect
     */
    public void startProcessing() throws Exception {
        isRunning = true;
        this.start();
    }

    /**
     * Stops processing the Log Sender Queue
     *
     * @throws Exception interrupted Exception if something goes down while stopping the thread.
     */
    public void stopProcessing() throws Exception {
        isRunning = false;
    }
}
