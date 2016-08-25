/**
 * @author : Vijet Badigannavar (bvijet@in.bosch.com)
 */
package com.bezirk.middleware.core.streaming.threads;

import com.bezirk.middleware.core.actions.ReceiveFileStreamAction;
import com.bezirk.middleware.core.streaming.PortFactory;
import com.bezirk.middleware.core.control.messages.streaming.StreamRequest;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.core.pubsubbroker.PubSubEventReceiver;
import com.bezirk.middleware.core.streaming.StreamManager;
import com.bezirk.middleware.core.streaming.port.StreamPortFactory;
import com.bezirk.middleware.core.util.ValidatorUtility;

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
 * This thread is used by the recipient that is interested in receiving the StreamDescriptor. This Thread opens socket at port ({@link StreamPortFactory#getPort(String)} and
 * waits for the sender to connect. Once the Sender gets connected, a file will be created at {} and will read
 * data at a time. After the data transfer it will release the port through
 * {@link StreamPortFactory#releasePort(int)}. From the {@link #streamLabel}, it will query the BezirkSadl
 * to get all the Zirk Identities via
 * corresponding to the services.
 * If error occurs during the course, it releases the port and closes the socket and Streams
 *
 * @see com.bezirk.proxy
 * @see StreamPortFactory
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
    private final String serializedMsg;
    private final PortFactory portFactory;
    private final PubSubEventReceiver pubSubReceiver;
    /*private final SphereSecurity sphereSecurity;*/
    private final String streamRequestKey;
    private StreamManager streamManager = null;

    /**
     * Constructor that is called during starting the thread
     *
     * @param  port   - port that this thread is listening to receive the data. { This port is got from StreamPortFactory }
     */
    public StreamReceivingThread(int port,/*String downloadPath,*/
                                 StreamRequest streamRequest, PortFactory portFactory,
                                 PubSubEventReceiver pubSubEventReceiver, /*SphereSecurity sphereSecurity,*/ StreamManager streamManager) {
        super();
        this.sphere = streamRequest.getSphereId();
        this.port = port;
        /*this.downloadPath = downloadPath;*/
        this.streamLabel = streamRequest.streamLabel;
        this.fileName = streamRequest.fileName;
        this.isEncrypted = streamRequest.isEncrypted;
        this.recipient = streamRequest.getRecipient();
        this.sender = streamRequest.getSender();
        this.serializedMsg = streamRequest.serialzedString;
        this.portFactory = portFactory;
        this.pubSubReceiver = pubSubEventReceiver;
        /*this.sphereSecurity = sphereSecurity;*/
        this.streamManager = streamManager;
        this.streamRequestKey = streamRequest.getUniqueKey();
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
            socket = new ServerSocket(port); // listen at the Port
            socket.setSoTimeout(CONNECTION_TIMEOUT_TIME);
            receivingSocket = socket.accept();

            tempFile = new File(getStreamDownloadPath() + fileName);
            fileOutputStream = new FileOutputStream(tempFile);
            inputStream = new DataInputStream(receivingSocket.getInputStream());

            //When the sphere security is implemented, this will be encrypt the stream byte content
            if (isEncrypted  /*&& sphereSecurity != null*/) {
                // message is encrypted and sphere security object is not null

                //sphereSecurity.decryptSphereContent(inputStream, fileOutputStream, sphere);
                logger.debug("---------- Secure Data transfer Completed! -------------");

            } else {
                int noOfBytesRead;
                final byte[] buffer = new byte[BUFFER_SIZE];
                while ((noOfBytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, noOfBytesRead);
                }
            }
            logger.debug("--- File Received--- & saved at "
                    + getStreamDownloadPath() + fileName);

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
            logger.error("Exception occurred while receiving stream.", e);
            if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
                logger.error("Failed to delete temporary stream file: {}", tempFile);
            }
        } finally {
            if (streamErrored) {
                if (!portFactory.releasePort(port)) {
                    logger.error("Error releasing Port Connection.");
                }
            }

            closeResources(socket, receivingSocket, fileOutputStream, inputStream, streamRequestKey);
        }
    }

    private void closeResources(ServerSocket socket, Socket receivingSocket,
                                FileOutputStream fileOutputStream, DataInputStream inputStream, String streamRequestKey) {
        try {
            if (ValidatorUtility.isObjectNotNull(inputStream)) {
                inputStream.close();
            }

            if (ValidatorUtility.isObjectNotNull(fileOutputStream)) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            if (ValidatorUtility.isObjectNotNull(receivingSocket)) {                                     // If SocketTimeout Exception occurs, receivingSocket==null
                receivingSocket.close();
            }
            if (ValidatorUtility.isObjectNotNull(socket)) {                                              // If SocketTimeout Exception occurs, socket==null
                socket.close();
            }

            //Punith clean the active streamMap
            streamManager.removeRefFromActiveStream(streamRequestKey);
        } catch (IOException e) {

            logger.error("Exception in closing resources.", e);
        }
    }

    private void notifyStreamFile(File tempFile, boolean portReleased) {
        if (portReleased) {
            ReceiveFileStreamAction uStreamCallbackMsg = new ReceiveFileStreamAction(
                    recipient.zirkId, serializedMsg,
                    tempFile, sender);
            if (ValidatorUtility.isObjectNotNull(pubSubReceiver)) {

                pubSubReceiver.processNewStream(uStreamCallbackMsg);

            } else {

                logger.error("BezirkCallback is not provided. Unable to send stream callback.");
            }
        } else {
            logger.error("Error releasing the Port");
        }
    }


    /**
     * creates the folder if not existing and cretes the file
     * @return
     */
    private String getStreamDownloadPath(){
        String downloadPath;
        ///storage/emulated/0/
        downloadPath= File.separator+"storage/emulated/0/" + "downloads" + File.separator;
        final File createDownloadFolder = new File(
                downloadPath);
        if (!createDownloadFolder.exists()) {
            if (!createDownloadFolder.mkdir()) {
                logger.error("Failed to create download direction: {}",
                        createDownloadFolder.getAbsolutePath());
            }
        }
        return downloadPath;
    }

}