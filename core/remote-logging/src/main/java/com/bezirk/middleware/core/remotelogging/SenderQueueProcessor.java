/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.remotelogging;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Locale;
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
     * Blocking Queue that is used to queue logger messages at the logging client.
     */
    private static final BlockingQueue<String> logSenderQueue = new SynchronousQueue<>();
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
    private Socket logClientSocket = null;

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
                logger.error("Remote logger sender queue interrupted", e);
                isRunning = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Starts Processing the Log Sender Queue
     */
    public boolean startProcessing() {
        isRunning = true;
        try {
            logClientSocket = new Socket(remoteServiceIP, remoteServicePort);
        } catch (IOException e) {
            final String logMessage = String.format(Locale.getDefault(),
                    "Failed to create logging client: remoteServiceIP = %s, port %d",
                    remoteServiceIP, remoteServicePort);
            logger.error(logMessage, e);
            return false;
        }
        new Thread(this).start();
        return true;
    }

    /**
     * Stops processing the Log Sender Queue
     *
     * @throws IOException IOException if something goes down while stopping the thread.
     */
    public boolean stopProcessing() throws IOException {
        isRunning = false;
        clearQueue();
        if (logClientSocket != null) {
            logClientSocket.close();
        }
        return false;
    }

    /**
     * loads the serialized RemoteLogMessage into LogSenderQueue
     *
     * @param serializedLogMsg serialized Log Message
     * @throws InterruptedException if multiple threads try to access the queue.
     */
    public void processLogOutMessage(String serializedLogMsg) throws InterruptedException {
        logSenderQueue.put(serializedLogMsg);
    }

    /**
     * Waits on the logSenderQueue to retrieve the logger Message
     *
     * @return String representation of the RemoteLogMessage
     * @throws InterruptedException if multiple threads try to access the queue.
     */
    private StringBuilder getLogOutgoingMessage() throws InterruptedException {
        return new StringBuilder(logSenderQueue.take());
    }

    /**
     * clears the logSenderQueue
     */
    void clearQueue() {
        logSenderQueue.clear();
    }
}
