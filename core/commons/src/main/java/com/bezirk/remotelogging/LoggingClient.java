/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 2/17/2015
 */
package com.bezirk.remotelogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging Client that is used to send the log message to the remote Logging Service. Client will
 * be activated(started)/ Deactivated (Stopped) / Updated after LoggingService Message
 * is received from the Logging Service.
 */
public class LoggingClient {
    /**
     * private logger for the class
     */
    private static final Logger log = LoggerFactory.getLogger(LoggingClient.class);
    /**
     * Remote Logging Service IP
     */
    private String serviceIP = null;
    /**
     * Remote Logging Service Port
     */
    private int servicePort = -1;
    /**
     * Processor for LogSenderQueue
     */
    private LogSenderQueueProcessor logSenderQueueProcessor = null;

    /**
     * Starts the client and the log sender Processor.
     *
     * @param remoteIP - IP of the logging Service
     * @param port     - Port at which the logging Service is listening
     */
    public void startClient(String remoteIP, int port) throws Exception {
        this.serviceIP = remoteIP;
        this.servicePort = port;
        logSenderQueueProcessor = new LogSenderQueueProcessor(this.serviceIP, this.servicePort);
        logSenderQueueProcessor.startProcesing();
    }

    /**
     * Shuts the logging Client.
     *
     * @param remoteIP Ip of the logging service that is shutting
     * @param port     port at which the logging service was listening for the clients
     */
    public void stopClient(String remoteIP, int port) throws Exception {
        if (null != logSenderQueueProcessor && remoteIP.equals(this.serviceIP) && port == this.servicePort) {
            try {
                LoggingQueueManager.clearLogSenderQueue();
                logSenderQueueProcessor.stopProcessing();
            } catch (Exception e) {
                throw e;
            } finally {
                logSenderQueueProcessor = null;
                serviceIP = null;
                servicePort = -1;
            }
            return;
        }
        throw new Exception("Tried to stop the client that is not started");
    }

    /**
     * Updates the Logging Client with new Logging Service Properties.
     *
     * @param newIP IP address of the new Logging Service
     * @param port  Port at which the Logging Service is listening
     */
    public void updateClient(String newIP, int port) throws Exception {
        if (!this.serviceIP.equals(newIP) || this.servicePort != port) {
            stopClient(this.serviceIP, this.servicePort);
            startClient(newIP, port);
            return;
        }
        log.debug("Received same LoggingService request to update the client");
    }


}
