/**
 *@author : Vijet Badigannavar (bvijet@in.bosch.com)
 */
package com.bezirk.streaming.threads;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.comms.UhuComms;
import com.bezirk.sadl.ISadlEventReceiver;
import com.bezirk.sphere.api.IUhuSphereForSadl;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.comms.IPortFactory;
import com.bezirk.streaming.port.PortFactory;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.util.UhuValidatorUtility;

/**
 * This thread is used by the recipient that is interested in receiving the Stream. This Thread opens socket at port ({@link PortFactory#getPort(String)} and
 * waits for the sender to connect. Once the Sender gets connected, a file will be created at {@link UhuComms#DOWNLOAD_PATH+this#fileName} and will read
 * data at a time. After the data transfer it will release the port through {@link PortFactory#releasePort(int)}. From the {@link this#streamLabel}, it will query the UhuSadl
 * to get all the Service Identities via
 * corresponding to the services.
 * If error occurs during the course, it releases the port and closes the socket and Streams  
 *
 *  @see com.bezirk.proxy
 *  @see UhuComms
 *  @see PortFactory
 */
public class StreamReceivingThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(StreamReceivingThread.class);

    /*Connection Timeout is hard coded to 45 sec. When set, if the requester doesn't get connected with this time, then the socket throws an Exception and
     * the port is released*/
    private static final int CONNECTION_TIMEOUT_TIME = 45000;                                     // 45 sec
    private static final int BUFFER_SIZE = 1024;
	private final String sphere;
    private final int port;                                                                      // port,received from Port Factory
    private final String streamLabel;                                                                 // StreamLabel, that the service has subscribed
    private final String fileName;
    private final boolean isSecure;
    private final UhuServiceEndPoint recipient;
    private final UhuServiceEndPoint sender;
    private final String serialzedMsg;
    private final short streamId;
    private final IPortFactory portFactory;
    private final ISadlEventReceiver sadlReceiver;
    private final IUhuSphereForSadl sphereForSadl ;

    /**
     * Constructor that is called during starting the thread
     * @param sphereForSadl 
     * @param - sphere - used to decrypt the data
     * @param - port   - port that this thread is listening to receive the data. { This port is got from PortFactory }
     * @param - streamLabel - streamLabel that is used to identify the Stream
     */
    public StreamReceivingThread(int port,
            StreamRequest streamRequest, IPortFactory portFactory,
            ISadlEventReceiver sadlReceiver, IUhuSphereForSadl sphereForSadl) {
        super();
        this.sphere =streamRequest.getSphereId();
        this.port = port;
        this.streamLabel = streamRequest.streamLabel;
        this.fileName = streamRequest.fileName;
        this.isSecure = streamRequest.isSecure;
        this.recipient = streamRequest.getRecipient();
        this.sender = streamRequest.getSender();
        this.serialzedMsg = streamRequest.serialzedString;
        this.streamId = streamRequest.localStreamId;
        this.portFactory = portFactory;
        this.sadlReceiver = sadlReceiver;
        this.sphereForSadl = sphereForSadl;
    }

    @Override
    public void run() {

        ServerSocket socket = null;
        Socket receivingSocket = null;
        File tempFile = null;
        FileOutputStream fileOutputStream = null;
        DataInputStream inputStream = null;
        boolean portReleased = false;

        boolean streamErrored = true;
        
        try {
            LOGGER.debug("Thread started to listen at port to receive Data..");
            socket = new ServerSocket(port);                                      // listen at the Port
            socket.setSoTimeout(CONNECTION_TIMEOUT_TIME);
            receivingSocket = socket.accept();

            tempFile = new File(UhuComms.getDOWNLOAD_PATH() + fileName);
            fileOutputStream = new FileOutputStream(tempFile);
            inputStream = new DataInputStream(receivingSocket.getInputStream());

            if (isSecure) {
            	if(UhuValidatorUtility.isObjectNotNull(sphereForSadl)){
            		sphereForSadl.decryptSphereContent(inputStream, fileOutputStream, sphere);
            	}else{
            		LOGGER.error("SphereForSadl is not initialized. Unable to process secure streaming request.");
            	}
            	
            	LOGGER.debug("---------- Secure Data transfer Completed! -------------");
            } else {
                int noOfBytesRead = 0;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((noOfBytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, noOfBytesRead);
                }
            }
            LOGGER.debug("--- File Received--- & saved at "
                    + UhuComms.getDOWNLOAD_PATH() + fileName);

            portReleased = portFactory.releasePort(port);                                 // release the Port
            notifyStreamFile(tempFile, portReleased);
            streamErrored  =false;
        } catch (SocketException e) {
            LOGGER.error(
                    "Connection Timeout, the client didn't connect within specified timeout",
                    e);
            if (UhuValidatorUtility.isObjectNotNull(tempFile) && tempFile.exists()) {
                tempFile.delete();
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(
                    "Unable to receiving stream file in downloads.",
                    e);
            if (UhuValidatorUtility.isObjectNotNull(tempFile) && tempFile.exists()) {
                tempFile.delete();
            }
        } catch (IOException e) {
            LOGGER.error(
                    "Exception occured while receiving stream.",
                    e);
            if (UhuValidatorUtility.isObjectNotNull(tempFile) && tempFile.exists()) {
                tempFile.delete();
            }
        }  finally {
            
            if (streamErrored) {
                portReleased = portFactory.releasePort(port); // release the port if there is any exception
                if (!portReleased) {
                    LOGGER.error("Error releasing Port Connection.");
                }
            }
            closeResources(socket, receivingSocket, fileOutputStream,
                    inputStream);
        }
    }

    private void closeResources(ServerSocket socket, Socket receivingSocket,
            FileOutputStream fileOutputStream, DataInputStream inputStream) {
        try {
            if (UhuValidatorUtility.isObjectNotNull(inputStream)) {
                inputStream.close();
            }

            if (UhuValidatorUtility.isObjectNotNull(fileOutputStream)) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            if (UhuValidatorUtility.isObjectNotNull(receivingSocket)) {                                     // If SocketTimeout Exception occurs, receivingSocket==null
                receivingSocket.close();
            }
            if (UhuValidatorUtility.isObjectNotNull(socket)) {                                              // If SocketTimeout Exception occurs, socket==null
                socket.close();
            }
        } catch (IOException e) {

            LOGGER.error("Exception in closing resources.", e);
        }
    }

    private void notifyStreamFile(File tempFile, boolean portRealeased) {
        if (portRealeased) {
            StreamIncomingMessage uStreamCallbackMsg = new StreamIncomingMessage(
                    recipient.serviceId, streamLabel, serialzedMsg,
                    tempFile.getAbsolutePath(), streamId, sender);
            if (UhuValidatorUtility.isObjectNotNull(sadlReceiver)) {

                sadlReceiver.processNewStream(uStreamCallbackMsg);

            } else {

                LOGGER.error("UhuCallback is not provided. Unable to send stream callback.");
            }
        } else {
            LOGGER.error("Error releasing the Port");
        }
    }

}