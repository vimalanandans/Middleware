package com.bezirk.rest;

/**
 * 
 * @author PIK6KOR
 *
 */
public class CommsRestController implements IHttpComms {

	private CommsHttpServer commsHttpServer;
	
	public CommsRestController() {
		commsHttpServer = CommsHttpServer.getInstance();
	}
	
	/**
	 * start the HttpComms
	 */
	@Override
	public boolean startHttpComms() {
		return commsHttpServer.startServer();
	}
	
	
	/**
	 * Stop the HTTP Comms.
	 */
	@Override
	public boolean stopHttpComms() {
		return commsHttpServer.stopServer();
	}
	
	@Override
	public boolean isServerRunning() {
		return commsHttpServer.isAlive();
	}
	
	
	/**
	 * Serve the request, required
	 *//*
	@Override
	public String serveRequest(String request) {
		//Here call the comms and server the request ... ?
		
		
		
		
		//**** check what happens to Sadl and Sphere... ***
		
		
		
		return null;
	}*/
	
	/**
	 * to add the URI dynamically... but will be stoped and stared again!!
	 * @param routeUri
	 * @return
	 */
	public boolean addMapping(String routeUri){
		
		return false;
	}

}
