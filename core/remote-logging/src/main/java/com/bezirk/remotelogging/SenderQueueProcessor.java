package com.bezirk.remotelogging;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Processes the LogSenderQueue. It makes a blocking call on the Log sender Queue and waits for the queue to be updated.
 * It retrieve the String from the LogSenderQueue and pushes the message onto the Logging Zirk
 * and removes it from the queue.
 */
public class SenderQueueProcessor implements Runnable {
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

    Socket logClientSocket = null;

    /**
     * Blocking Queue that is used to queue logger messages at the logging client.
     */
    private static BlockingQueue<String> logSenderQueue = null;

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
                StringBuilder logMsgString = getLogOutgoingMessage();

                try {
                    DataOutputStream clientOutputStream = new DataOutputStream(logClientSocket.getOutputStream());
                    clientOutputStream.writeBytes(logMsgString.toString());
                    clientOutputStream.flush();
                    clientOutputStream.close();
                } catch (IOException e) {
                    logger.error("Some Error occurred :", e);
                } finally {
                    try {
                        if (logClientSocket != null) {
                            logClientSocket.close();
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
    public boolean startProcessing() throws Exception {
        isRunning = true;
        try {
            logClientSocket = new Socket(remoteServiceIP, remoteServicePort);
        }catch  (IOException e){
            logger.error("logging client create fail. ip : "+ remoteServiceIP + " port "
                    + String.valueOf(remoteServicePort) + " " +e);
            return false;
        }
        Thread t = new Thread(this);
        t.start();
        return true;
    }

    /**
     * Stops processing the Log Sender Queue
     *
     * @throws Exception interrupted Exception if something goes down while stopping the thread.
     */
    public boolean stopProcessing() throws Exception {
        clearQueue();
        if (logClientSocket != null) {
            logClientSocket.close();
        }
        isRunning = false;
        return isRunning;
    }
    /**
     * loads the serialized RemoteLogMessage into LogSenderQueue
     *
     * @param serializedLogMsg serialized Log Message
     * @throws InterruptedException if multiple threads try to access the queue.
     */
    public void processLogOutMessage(String serializedLogMsg) throws InterruptedException {
        if (logSenderQueue == null) {
            logSenderQueue = new SynchronousQueue<String>();
        }
        logSenderQueue.put(serializedLogMsg);
    }

    /**
     * Waits on the logSenderQueue to retrieve the logger Message
     *
     * @return String representation of the RemoteLogMessage
     * @throws InterruptedException if multiple threads try to access the queue.
     */
    private  StringBuilder getLogOutgoingMessage() throws InterruptedException {
        if (logSenderQueue == null) {
            logSenderQueue = new SynchronousQueue<String>();
        }
        return new StringBuilder(logSenderQueue.take());
    }

    /**
     * clears the logSenderQueue
     */
    void clearQueue()
    {
        if (logSenderQueue == null) {
            logSenderQueue = new SynchronousQueue<String>();
        }
        logSenderQueue.clear();
    }
}
