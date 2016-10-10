package com.bezirk.middleware.core.remotelogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is an Bezirk logging Zirk. All Platforms have to use this zirk to enable
 * logging on that platform.<p>
 * This zirk will listen at a particular port and accept the logging clients.
 * The logging client send the serialized Logging Message. The Zirk accepts the connection and
 * loads the serialized message into ReceiverQueue.
 */
public class RemoteLoggingServer extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(RemoteLoggingServer.class);
    /**
     * TCP listening Port for the zirk. allocated automatically
     */
    private int listeningPort = 0;
    /**
     * Flag to start stop the Zirk
     */
    private boolean isRunning = false;
    /**
     * Socket at which the Zirk is listening
     */
    private ServerSocket serverSocket = null;
    private ReceiverQueueProcessor receiverQueueProcessor;
    private RemoteLoggingMessageNotification remoteLoggingMessageNotification = null;
    private boolean enableFileLogging;

    /**
     * setup the port.
     */
    public RemoteLoggingServer(RemoteLoggingMessageNotification remoteLoggingMessageNotification, boolean enableFileLogging) {
        this.remoteLoggingMessageNotification = remoteLoggingMessageNotification;

        this.enableFileLogging = enableFileLogging;
    }

    @Override
    public void run() {
        logger.trace("Logging Zirk is being Started in remote logging");
        try {
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                String serializedLoggerMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8")).readLine();
                receiverQueueProcessor.processLogInMessage(serializedLoggerMessage);
            }
        } catch (IOException e) {
            logger.error("Exception reading socket in remote logging server", e);
        } catch (InterruptedException e) {
            logger.error("Remote logging server socket reader interrupted", e);
            isRunning = false;
            Thread.currentThread().interrupt();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.error("Exception occurred while closing the serverSocket", e);
                }
            }
        }
    }


    public int getPort() {
        return listeningPort;
    }

    /**
     * Starts the Logging Zirk
     */
    public boolean startRemoteLoggingService() {

        // find a free port first
        listeningPort = startServer(listeningPort);
        if (listeningPort == 0) {
            logger.error("unable to allocate the free port for remote logging server");
            return false;
        }

        receiverQueueProcessor = new ReceiverQueueProcessor(remoteLoggingMessageNotification, enableFileLogging);
        receiverQueueProcessor.startProcessing();

        logger.info("remote logging server started on " + listeningPort);
        isRunning = true;
        this.start();
        return true;
    }

    /***
     * Start the server and return the port
     */
    private int startServer(int portNumber) {
        try {
            serverSocket = new ServerSocket(portNumber);
            serverSocket.setReuseAddress(true);
            portNumber = serverSocket.getLocalPort();
        } catch (IOException e) {
            logger.error("Failed to start remote logging server", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.error("Failed to close remote logging server socket after starting " +
                            "server failed", e);
                }
            }
        }
        return portNumber;
    }

    /**
     * Stops the Logging Zirk
     *
     * @throws IOException if there is an error while closing the server socket
     */
    public void stopRemoteLoggingService() throws IOException {
        receiverQueueProcessor.stopProcessing();
        receiverQueueProcessor = null;
        isRunning = false;
        serverSocket.close();
    }
}
