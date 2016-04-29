/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 3/30/2015
 */
package com.bezirk.remotelogging.manager;

import com.bezirk.remotelogging.client.LoggingClient;
import com.bezirk.remotelogging.loginterface.BezirkLogging;
import com.bezirk.remotelogging.processors.LogReceiverQueueProcessor;
import com.bezirk.remotelogging.service.BezirkLoggingService;


/**
 * Logging Manager class that starts/stops LoggingServices and LoggingClient.
 * The platforms need to instantiate this manager and can start stop the zirk.
 */
public final class BezirkLoggingManager {
    /**
     * BezirkLoggingService
     */
    private BezirkLoggingService bezirkLoggingService = null;
    /**
     * LogReceiverProcessor used by the Logging Zirk
     */
    private LogReceiverQueueProcessor receiverQueueProcessor = null;
    /**
     * Logging Client
     */
    private LoggingClient logClient = null;

    public BezirkLoggingManager() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Starts the Logging Zirk
     *
     * @param loggingPort             port at which the LoggingService Starts
     * @param platformSpecificHandler handler to give callback once the zirk receives the request
     * @throws Exception if handler is null, or something goes wrong while processing.
     */
    public void startLoggingService(final int loggingPort, final BezirkLogging platformSpecificHandler) throws Exception {
        if (bezirkLoggingService == null && platformSpecificHandler != null) {
            bezirkLoggingService = new BezirkLoggingService(loggingPort);
            receiverQueueProcessor = new LogReceiverQueueProcessor(platformSpecificHandler);
            bezirkLoggingService.startLoggingService();
            receiverQueueProcessor.startProcessing();
            return;
        }
        throw new Exception("Tried to start LoggingService again, that is already started or Handler is null");
    }

    /**
     * Stops the logging Zirk
     *
     * @throws Exception if logging zirk is tried to stop that is not started
     */
    public void stopLoggingService() throws Exception {
        if (bezirkLoggingService != null) {
            receiverQueueProcessor.stopProcessing();
            bezirkLoggingService.stopLoggingService();
            bezirkLoggingService = null;
            receiverQueueProcessor = null;
            return;
        }
        throw new Exception("Logging zirk tried to stop that is not started");
    }

    /**
     * Starts the logging Client
     *
     * @param remoteIP   Ip of the remote logging zirk
     * @param remotePort port of the remote logging zirk
     * @throws Exception if parameters are wrong or zirk is not available.
     */
    public void startLoggingClient(String remoteIP, int remotePort) throws Exception {
        if (logClient == null) {
            logClient = new LoggingClient();
            logClient.startClient(remoteIP, remotePort);
            return;
        }
        logClient.updateClient(remoteIP, remotePort);
    }

    /**
     * Stops the logging client
     *
     * @param remoteIP   IP of the remote zirk that is shutting
     * @param remotePort Port of the remote zirk that is shutting
     * @throws Exception if tried to stop the client that is not started
     */
    public void stopLoggingClient(String remoteIP, int remotePort) throws Exception {
        if (logClient != null) {
            logClient.stopClient(remoteIP, remotePort);
            logClient = null;
        }
    }
}
