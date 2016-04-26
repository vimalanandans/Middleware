/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 2/17/2015
 */
package com.bezirk.remotelogging.queues;

import com.bezirk.remotelogging.service.UhuLoggingService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Manager Class that manages <b>singleton<b> queues used by the stack to send and receive the
 * Log messages (UhuLoggingMessage). The Processors defined in the {@link com.bezirk.remotelogging.processors}
 * will process these queues accordingly.
 */
@SuppressWarnings("PMD")// inorder to avoid the synchonized 
public final class LoggingQueueManager {
    /**
     * Blocking Queue that is used to queue logger messages at the logging client.
     */
    private static BlockingQueue<String> logSenderQueue = null;
    /**
     * Blocking Queue that is used to queue logger messages at the Logging Zirk.
     *
     * @see UhuLoggingService
     */
    private static BlockingQueue<String> logReceiverQueue = null;

    /**
     * Private Constructor to make this Utility class
     */
    private LoggingQueueManager() {
    }

    /**
     * loads the serialized UhuLoggingMessage into LogSenderQueue
     *
     * @param serializedLogMsg serialized Log Message
     * @throws InterruptedException if multiple threads try to access the queue.
     */
    public static void loadLogSenderQueue(String serializedLogMsg) throws InterruptedException {
        if (logSenderQueue == null) {
            logSenderQueue = new SynchronousQueue<String>();
        }
        logSenderQueue.put(serializedLogMsg);
    }

    /**
     * Waits on the logSenderQueue to retrive the logger Message
     *
     * @return String representation of the UhuLoggingMessage
     * @throws InterruptedException if multiple threads try to access the queue.
     */
    public static StringBuilder fetchFromLogSenderQueue() throws InterruptedException {
        if (logSenderQueue == null) {
            logSenderQueue = new SynchronousQueue<String>();
        }
        return new StringBuilder(logSenderQueue.take());
    }

    /**
     * loads the serialized UhuLoggingMessage into LogReceiverQueue
     *
     * @param serializedLogMsg serialized Log Message
     * @throws InterruptedException if multiple threads try to access the queue.
     */
    public static void loadLogReceiverQueue(String serializedLogMsg) throws InterruptedException {
        if (logReceiverQueue == null) {
            logReceiverQueue = new SynchronousQueue<String>();
        }
        logReceiverQueue.put(serializedLogMsg);
    }

    /**
     * Waits on the logReceiverQueue to retrieve the logger Message
     *
     * @return String representation of the UhuLoggingMessage
     * @throws InterruptedException if multiple threads try to access the queue.
     */
    public static StringBuilder fetchFromLogReceiverQueue() throws InterruptedException {
        if (logReceiverQueue == null) {
            logReceiverQueue = new SynchronousQueue<String>();
        }
        return new StringBuilder(logReceiverQueue.take());
    }

    /**
     * clears the logSenderQueue
     */
    public static void clearLogSenderQueue() {
        if (logSenderQueue == null) {
            logSenderQueue = new SynchronousQueue<String>();
        }
        logSenderQueue.clear();
    }
}
