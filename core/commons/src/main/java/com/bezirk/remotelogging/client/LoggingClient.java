package com.bezirk.remotelogging.client;

import com.bezirk.remotelogging.processors.LogSenderQueueProcessor;
import com.bezirk.remotelogging.queues.LoggingQueueManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging Client that is used to send the logger message to the remote Logging Zirk. Client will
 * be activated(started)/ Deactivated (Stopped) / Updated after LoggingService Message
 * is received from the Logging Zirk.
 */
public class LoggingClient {
    /**
     * private logger for the class
     */
    private static final Logger logger = LoggerFactory.getLogger(LoggingClient.class);
    /**
     * Remote Logging Zirk IP
     */
    private String serviceIP = null;
    /**
     * Remote Logging Zirk Port
     */
    private int servicePort = -1;
    /**
     * Processor for LogSenderQueue
     */
    private LogSenderQueueProcessor logSenderQueueProcessor = null;

    /**
     * Starts the client and the logger sender Processor.
     *
     * @param remoteIP - IP of the logging Zirk
     * @param port     - Port at which the logging Zirk is listening
     */
    public void startClient(String remoteIP, int port) throws Exception {
        this.serviceIP = remoteIP;
        this.servicePort = port;
        logSenderQueueProcessor = new LogSenderQueueProcessor(this.serviceIP, this.servicePort);
        logSenderQueueProcessor.startProcessing();
    }

    /**
     * Shuts the logging Client.
     *
     * @param remoteIP Ip of the logging zirk that is shutting
     * @param port     port at which the logging zirk was listening for the clients
     */
    public void stopClient(String remoteIP, int port) throws Exception {
        if (null != logSenderQueueProcessor && remoteIP.equals(this.serviceIP) && port == this.servicePort) {
            LoggingQueueManager.clearLogSenderQueue();
            logSenderQueueProcessor.stopProcessing();
            logSenderQueueProcessor = null;
            serviceIP = null;
            servicePort = -1;
            
            return;
        }
        throw new Exception("Tried to stop the client that is not started");
    }

    /**
     * Updates the Logging Client with new Logging Zirk Properties.
     *
     * @param newIP IP address of the new Logging Zirk
     * @param port  Port at which the Logging Zirk is listening
     */
    public void updateClient(String newIP, int port) throws Exception {
        if (!this.serviceIP.equals(newIP) || this.servicePort != port) {
            stopClient(this.serviceIP, this.servicePort);
            startClient(newIP, port);
            return;
        }
        logger.debug("Received same LoggingService request to update the client");
    }


}
