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
    private final FileLogger fileLogger;
    /**
     * Flag used for starting/ Stopping Threads!
     */
    private boolean isRunning = false;
    private Gson gson = null;
    /**
     * Blocking Queue that is used to queue logger messages at the Logging Zirk.
     *
     * @see RemoteLoggingServer
     */
    private BlockingQueue<String> logReceiverQueue = null;


    /**
     * Setup the Processor.
     *
     * @param logger platform Specific Logger that is used to update the UI.
     */
    public ReceiverQueueProcessor(RemoteLoggingMessageNotification logger, boolean enableFileLogging) {
        this.platformSpecificLogger = logger;

        if (enableFileLogging) {
            fileLogger = new FileLogger();
        } else {
            fileLogger = null;
        }

        gson = new Gson();
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                // read the blocked queue to fetch the incoming message
                final StringBuilder logMsgString = getLogIncomingMessage();

                RemoteLoggingMessage logMsg = gson.fromJson(logMsgString.toString(), RemoteLoggingMessage.class);
                if (Util.LOGGING_VERSION.equals(logMsg.version)) {
                    platformSpecificLogger.handleLogMessage(logMsg);

                    // log into file
                    if (fileLogger != null)
                        fileLogger.handleLogMessage(logMsg);

                } else {
                    logger.error("LOGGING VERSION MISMATCH!!" + "Received LOG MSG VERSION = " + logMsg.version +
                            " CURRENT LOGGING VERSION: " + Util.LOGGING_VERSION);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Log receiver interrupted", e);
            isRunning = false;
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Starts Processing the LogReceiverQueue
     */
    public void startProcessing() {
        logger.trace("startProcessing of ReceiverQueueProcessor");

        if (!isRunning) {
            final Thread t = new Thread(this);
            t.start();
            isRunning = true;
        }
    }

    /**
     * Stop processing the LogReceiverQueue.
     */
    public void stopProcessing() {
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
            logReceiverQueue = new SynchronousQueue<>();
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
            logReceiverQueue = new SynchronousQueue<>();
        }
        return new StringBuilder(logReceiverQueue.take());
    }


}
