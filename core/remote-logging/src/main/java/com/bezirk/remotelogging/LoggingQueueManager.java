package com.bezirk.remotelogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Logging queues managers
 * logReceiverQueue - which holds the incoming log messages and sends those to notifications
 *  logSenderQueue - which holds the outgoing queue and sends those to server
*/
public final class LoggingQueueManager {
    private  final Logger logger = LoggerFactory.getLogger(LoggingQueueManager.class);



}
