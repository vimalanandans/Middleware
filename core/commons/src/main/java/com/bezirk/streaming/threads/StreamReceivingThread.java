/**
 * @author : Vijet Badigannavar (bvijet@in.bosch.com)
 */
package com.bezirk.streaming.threads;

import com.bezirk.comms.BezirkComms;
import com.bezirk.comms.IPortFactory;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.sadl.ISadlEventReceiver;
import com.bezirk.sphere.api.IUhuSphereForSadl;
import com.bezirk.streaming.port.PortFactory;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * This thread is used by the recipient that is interested in receiving the Stream. This Thread opens socket at port ({@link PortFactory#getPort(String)} and
 * waits for the sender to connect. Once the Sender gets connected, a file will be created at {@link BezirkComms#DOWNLOAD_PATH+this#fileName} and will read
 * data at a time. After the data transfer it will release the port through {@link PortFactory#releasePort(int)}. From the {@link this#streamLabel}, it will query the UhuSadl
 * to get all the Zirk Identities via
 * corresponding to the services.
 * If error occurs during the course, it releases the port and closes the socket and Streams
 *
 * @see com.bezirk.proxy
 * @see BezirkComms
 * @see PortFactory
 */
public class StreamReceivingThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(StreamReceivingThread.class);

    /*Connection Timeout is hard coded to 45 sec. When set, if the requester doesn't get connected with this time, then the socket throws an Exception and
     * the port is released*/
    private static final int CONNECTION_TIMEOUT_TIME = 45000;                                     // 45 sec
    private static final int BUFFER_SIZE = 1024;
    private final String sphere;
    private final int port;                                                                      // port,received from Port Factory
    private final String streamLabel;                                                                 // StreamLabel, that the zirk has subscribed
    private final String fileName;
    private final boolean isEncrypted;
    private final BezirkZirkEndPoint recipient;
    private final BezirkZirkEndPoint sender;
    private final String serialzedMsg;
    private final short streamId;
    private final IPortFactory portFactory;
    private final ISadlEventReceiver sadlReceiver;
    private final IUhuSphereForSadl sphereForSadl;

    /**
     * Constructor that is called during starting the thread
     *
     * @param sphereForSadl
     * @param -             sphere - used to decrypt the data
     * @param -             port   - port that this thread is listening to receive the data. { This port is got from PortFactory }
     * @param -             streamLabel - streamLabel that is used to identify the Stream
     */
    public StreamReceivingThread(int port,
                                 StreamRequest streamRequest, IPortFactory portFactory,
                                 ISadlEventReceiver sadlReceiver, IUhuSphereForSadl sphereForSadl) {
        super();
        this.sphere = streamRequest.getSphereId();
        this.port = port;
        this.streamLabel = streamRequest.streamLabel;
        this.fileName = streamRequest.fileName;
        this.isEncrypted = streamRequest.isEncrypted;
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

        boolean streamErrored = true;

        try {
            logger.debug("Thread started to listen at port to receive Data..");
            socket = new ServerSocket(port);                                      // listen at the Port
            socket.setSoTimeout(CONNECTION_TIMEOUT_TIME);
            receivingSocket = socket.accept();

            tempFile = new File(BezirkComms.getDOWNLOAD_PATH() + fileName);
            fileOutputStream = new FileOutputStream(tempFile);
            inputStream = new DataInputStream(receivingSocket.getInputStream());

            if (isEncrypted) {
                if (BezirkValidatorUtility.isObjectNotNull(sphereForSadl)) {
                    sphereForSadl.decryptSphereContent(inputStream, fileOutputStream, sphere);
                } else {
                    logger.error("SphereForSadl is not initialized. Unable to process secure streaming request.");
                }

                logger.debug("---------- Secure Data transfer Completed! -------------");
            } else {
                int noOfBytesRead;
                final byte[] buffer = new byte[BUFFER_SIZE];
                while ((noOfBytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, noOfBytesRead);
                }
            }
            logger.debug("--- File Received--- & saved at "
                    + BezirkComms.getDOWNLOAD_PATH() + fileName);

            notifyStreamFile(tempFile, portFactory.releasePort(port));
            streamErrored = false;
        } catch (SocketException e) {
            logger.error("Connection Timeout, the client didn't connect within specified timeout", e);
            if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
                logger.error("Failed to delete temporary stream file: {}", tempFile);
            }
        } catch (FileNotFoundException e) {
            logger.error("Unable to receiving stream file in downloads.", e);
            if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
                logger.error("Failed to delete temporary stream file: {}", tempFile);
            }
        } catch (IOException e) {
            logger.error("Exception occured while receiving stream.", e);
            if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
                logger.error("Failed to delete temporary stream file: {}", tempFile);
            }
        } finally {
            if (streamErrored) {
                if (!portFactory.releasePort(port)) {
                    logger.error("Error releasing Port Connection.");
                }
            }

            closeResources(socket, receivingSocket, fileOutputStream, inputStream);
        }
    }

    private void closeResources(ServerSocket socket, Socket receivingSocket,
                                FileOutputStream fileOutputStream, DataInputStream inputStream) {
        try {
            if (BezirkValidatorUtility.isObjectNotNull(inputStream)) {
                inputStream.close();
            }

            if (BezirkValidatorUtility.isObjectNotNull(fileOutputStream)) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            if (BezirkValidatorUtility.isObjectNotNull(receivingSocket)) {                                     // If SocketTimeout Exception occurs, receivingSocket==null
                receivingSocket.close();
            }
            if (BezirkValidatorUtility.isObjectNotNull(socket)) {                                              // If SocketTimeout Exception occurs, socket==null
                socket.close();
            }
        } catch (IOException e) {

            logger.error("Exception in closing resources.", e);
        }
    }

    private void notifyStreamFile(File tempFile, boolean portRealeased) {
        if (portRealeased) {
            StreamIncomingMessage uStreamCallbackMsg = new StreamIncomingMessage(
                    recipient.zirkId, streamLabel, serialzedMsg,
                    tempFile, streamId, sender);
            if (BezirkValidatorUtility.isObjectNotNull(sadlReceiver)) {

                sadlReceiver.processNewStream(uStreamCallbackMsg);

            } else {

                logger.error("UhuCallback is not provided. Unable to send stream callback.");
            }
        } else {
            logger.error("Error releasing the Port");
        }
    }

}