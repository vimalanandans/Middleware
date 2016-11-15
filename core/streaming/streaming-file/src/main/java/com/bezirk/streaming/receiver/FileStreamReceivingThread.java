package com.bezirk.streaming.receiver;

import com.bezirk.middleware.core.util.ValidatorUtility;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;

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
 * Created by PIK6KOR on 11/15/2016.
 */

public class FileStreamReceivingThread implements Runnable{

    //assigned port for file streaming
    private Integer assignedPort = -1;

    //connection timeout
    private static final int CONNECTION_TIMEOUT_TIME = 45000;

    //file download path, pull this from the property file.
    private String filedownloadPath = null;

    private File file = null;

    //file stream buffer size.
    private static final int BUFFER_SIZE = 1024;

    // Note this has to be injected.
    FileStreamPortFactory portFactory = null;

    public FileStreamReceivingThread(Integer port, File file, FileStreamPortFactory portFactory){
        this.assignedPort = port;
        this.filedownloadPath = ""; //assign it from the property file.
        this.file = file;
        this.portFactory = portFactory;
    }

    private static final Logger logger = LoggerFactory.getLogger(FileStreamReceivingThread.class);

    @Override
    public void run() {

        ServerSocket socket = null;
        Socket receivingSocket = null;
        File tempFile = null;
        FileOutputStream fileOutputStream = null;
        DataInputStream inputStream = null;

        boolean streamErrored = true;

        try {
            socket = new ServerSocket(assignedPort); // listen at the Port
            socket.setSoTimeout(CONNECTION_TIMEOUT_TIME);
            receivingSocket = socket.accept();

            tempFile = new File(filedownloadPath + file);
            fileOutputStream = new FileOutputStream(tempFile);
            inputStream = new DataInputStream(receivingSocket.getInputStream());

            int noOfBytesRead;
            final byte[] buffer = new byte[BUFFER_SIZE];
            while ((noOfBytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, noOfBytesRead);
            }

            //release the port.
            portFactory.releasePort(assignedPort);

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
                //releasing the port from active and making it available.
                portFactory.releasePort(assignedPort);
            }

            closeResources(socket, receivingSocket, fileOutputStream, inputStream);
        }
    }

    /**
     * close the streaming resources.
     * @param socket
     * @param receivingSocket
     * @param fileOutputStream
     * @param inputStream
     */
    private void closeResources(ServerSocket socket, Socket receivingSocket,
                                FileOutputStream fileOutputStream, DataInputStream inputStream) {
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
        } catch (IOException e) {

            logger.error("Exception in closing resources.", e);
        }
    }


}
