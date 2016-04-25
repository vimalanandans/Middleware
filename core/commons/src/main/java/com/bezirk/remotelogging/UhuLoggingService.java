/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 2/17/2015
 */
package com.bezirk.remotelogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is an Uhu logging Service. All Platforms have to use this service to enable
 * logging on that platform.<p>
 * This service will listen at a particular port and accept the logging clients.
 * The logging client send the serialized Logging Message. The Service accepts the connection and
 * loads the serialized message into ReceiverQueue.
 */
public class UhuLoggingService extends Thread {
    /**
     * private logger for the class
     */
    private static final Logger logger = LoggerFactory.getLogger(UhuLoggingService.class);
    /**
     * TCP listening Port for the service
     */
    private final int listeningPort;
    /**
     * Flag to start stop the Service
     */
    private boolean isRunning = false;
    /**
     * Socket at which the Service is listening
     */
    private ServerSocket serverSocket = null;

    /**
     * setup the port.
     *
     * @param port at which the Service is listening for the clients to connect.
     */
    public UhuLoggingService(final int port) {
        this.listeningPort = port;
    }

    @Override
    public void run() {
        logger.info("Logging Service is being Started...");
        try {
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                String serializedLoggerMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
                LoggingQueueManager.loadLogReceiverQueue(serializedLoggerMessage);
            }
        } catch (IOException e) {
            logger.error("Some exception occured \n", e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.error("Exception occured while closing hte serverSocket \n", e);
                }
            }
        }
    }

    /**
     * Starts the Logging Service
     *
     * @throws IOException if socket is unavailable.
     */
    public void startLoggingService() throws IOException {
        isRunning = true;
        serverSocket = new ServerSocket(listeningPort);
        this.start();
    }

    /**
     * Stops the Logging Service
     *
     * @throws Exception if there is an error while closing the server socket
     */
    public void stopLoggingService() throws Exception {
        isRunning = false;
        serverSocket.close();
    }

}
