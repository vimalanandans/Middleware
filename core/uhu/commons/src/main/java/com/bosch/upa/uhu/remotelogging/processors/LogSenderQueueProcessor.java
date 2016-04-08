/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 2/17/2015
 */
package com.bosch.upa.uhu.remotelogging.processors;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.remotelogging.queues.LoggingQueueManager;

/**
 * Processes the LogSenderQueue. It makes a blocking call on the Log sender Queue and waits for the queue to be updated.
 * It retrieve the String from the LogSenderQueue and pushes the message onto the Logging Service 
 * and removes it from the queue.
 */
public class LogSenderQueueProcessor extends Thread {
	/**
	 * private logger for the class
	 */
	private final Logger log = LoggerFactory.getLogger(LogSenderQueueProcessor.class);
	/**
	 * Logging Service IP. This will set based on the LoggingMessage from the Logging service.
	 */
	private final String remoteServiceIP;
	/**
	 * Logging Service Port. This will set based on the LoggingMessage from the Logging service.
	 */
	private final int remoteServicePort;
	/**
	 * Flag used for starting/ Stopping Threads!
	 */
	private boolean isRunning = false;
	/**
	 * Setup the Logging Service Parameters
	 * @param remoteServiceIP IP address of the Logging Service
	 * @param remotePort Logging Service Port
	 */
	public LogSenderQueueProcessor(String remoteServiceIP, int remotePort) {
		this.remoteServiceIP = remoteServiceIP;
		this.remoteServicePort = remotePort;
	}
		
	@Override
	public void run() {
		while(isRunning){
			
			try{
				StringBuilder logMsgString = LoggingQueueManager.fetchFromLogSenderQueue();
				Socket uhuClient = null;
				try{
					uhuClient = new Socket(remoteServiceIP, remoteServicePort);
					DataOutputStream clientOutputStream = new DataOutputStream(uhuClient.getOutputStream());
					clientOutputStream.writeBytes(logMsgString.toString());
					clientOutputStream.flush();clientOutputStream.close();
				}catch(IOException ioExcpetion){
					log.error("Some Error occured :", ioExcpetion);
				}finally{
					try {
						if(uhuClient != null){
							uhuClient.close();
						}
					} catch (IOException e) {
						log.error("Errors occured in closing uhuClient \n", e);
					}
				}
			}catch(InterruptedException e){
				log.error(e.getMessage());
			}
		}
	}
	/**
	 * Starts Processing the Log Sender Queue
	 * @throws Exception if Logging Service is down and unable to connect
	 */
	public void startProcesing() throws Exception{
		isRunning=true;
		this.start();
	}
	/**
	 * Stops processing the Log Sender Queue
	 * @throws Exception intruppted Exception if something goes down while stopping the thread.
	 */
	public void stopProcessing() throws Exception{
		isRunning = false;
	}
}
