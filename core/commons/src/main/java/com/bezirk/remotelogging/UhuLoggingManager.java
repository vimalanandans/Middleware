/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 3/30/2015
 */
package com.bezirk.remotelogging;


/**
 * Logging Manager class that starts/stops LoggingServices and LoggingClient.
 * The platforms need to instantiate this manager and can start stop the service.
 */
public final class UhuLoggingManager {
	/**
	 * UhuLoggingService
	 */
	private UhuLoggingService uhuLoggingService = null;
    /**
     * LogReceiverProcessor used by the Logging Service
     */
    private LogReceiverQueueProcessor receiverQueueProcessor = null;
    /**
     * Logging Client
     */
    private LoggingClient logClient = null;
    
	public UhuLoggingManager() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Starts the Logging Service
	 * @param loggingPort port at which the LoggingService Starts
	 * @param platformSpecificHandler handler to give callback once the service receives the request
	 * @throws Exception if handler is null, or something goes wrong while processing.
	 */
	public void startLoggingService(final int loggingPort, final IUhuLogging platformSpecificHandler) throws Exception{
		if(uhuLoggingService == null && platformSpecificHandler != null){
			uhuLoggingService = new UhuLoggingService(loggingPort);
	        receiverQueueProcessor = new LogReceiverQueueProcessor(platformSpecificHandler);
	        uhuLoggingService.startLoggingService();
	        receiverQueueProcessor.startProcesing();
	        return;
		}
		throw new Exception("Tried to start LoggingService again,thats already started or Handler is null");
	}
	
	/**
	 * Stops the logging Service
	 * @throws Exception if logging service is tried to stop that is not started
	 */
	public void stopLoggingService() throws Exception{
		if(uhuLoggingService != null){
			receiverQueueProcessor.stopProcessing();
            uhuLoggingService.stopLoggingService();
            uhuLoggingService = null;
            receiverQueueProcessor = null;
            return;
		}
		throw new Exception("Logging service tried to stop that is not started");
	}
	
	/**
	 * Starts the logging Client
	 * @param remoteIP Ip of the remote logging service
	 * @param remotePort port of the remote logging service
	 * @throws Exception if parameters are wrong or service is not available.
	 */
	public void startLoggingClient(String remoteIP,int remotePort) throws Exception{
		if(logClient == null){
			logClient = new LoggingClient();
			logClient.startClient(remoteIP, remotePort);
			return;
		}
		logClient.updateClient(remoteIP, remotePort);
	}
	
	/**
	 * Stops the logging client
	 * @param remoteIP IP of the remote service that is shutting
	 * @param remotePort Port of the remote service that is shutting
	 * @throws Exception if tried to stop the client that is not started
	 */
	public void stopLoggingClient(String remoteIP,int remotePort) throws Exception{
		if(logClient != null){
			logClient.stopClient(remoteIP,remotePort);
			logClient = null;
		}
	}
}
