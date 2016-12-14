/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
import java.util.concurrent.Callable;

/**
 * FileStreamReceivingThread, will be a running thread
 *
 * Created by PIK6KOR on 11/15/2016.
 */

class FileStreamReceivingThread implements Runnable{

    //assigned port for file streaming
    private Integer assignedPort = -1;
    //connection timeout
    private static final int CONNECTION_TIMEOUT_TIME = 45000;

    //TODO file download path, pull this from the property file.
    private static final String fileDownloadFolder = "/storage/emulated/0/bezirk/downloads/";
    private File file = null;
    //file stream buffer size.
    private static final int BUFFER_SIZE = 1024;
    // Note this has to be injected.
    private FileStreamPortFactory portFactory = null;
    //logger instance
    private static final Logger logger = LoggerFactory.getLogger(FileStreamReceivingThread.class);

    FileStreamReceivingThread(Integer port, File file, FileStreamPortFactory portFactory){
        this.assignedPort = port;
        this.file = file;
        this.portFactory = portFactory;
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
            socket = new ServerSocket(assignedPort); // listen at the Port
            socket.setSoTimeout(CONNECTION_TIMEOUT_TIME);
            receivingSocket = socket.accept();

            if(getDownloadPath(fileDownloadFolder) != null){
                tempFile = new File(getDownloadPath(fileDownloadFolder) + file.getName());
                fileOutputStream = new FileOutputStream(tempFile);
                inputStream = new DataInputStream(receivingSocket.getInputStream());

                int noOfBytesRead;
                final byte[] buffer = new byte[BUFFER_SIZE];
                logger.debug("Strted file receiving thread for file {}", file.getName());
                while ((noOfBytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, noOfBytesRead);
                }

                //release the port.
                portFactory.releasePort(assignedPort);

                streamErrored = false;
            }else{
                logger.error("Failed to create folder!!!");
            }


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
     * @param socket open socket
     * @param receivingSocket receiving socket
     * @param fileOutputStream open stream which is open
     * @param inputStream input stream which was opened
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
            if (ValidatorUtility.isObjectNotNull(receivingSocket)) { // If SocketTimeout Exception occurs, receivingSocket==null
                receivingSocket.close();
            }
            if (ValidatorUtility.isObjectNotNull(socket)) { // If SocketTimeout Exception occurs, socket==null
                socket.close();
            }
        } catch (IOException e) {

            logger.error("Exception in closing resources.", e);
        }
    }

    /**
     * creates the folder if not existing and returns the download path for the respective environment
     * @return downloadFolder Path of the download folder.
     */
    private String getDownloadPath(String downloadFolder){
        final File createDownloadFolder = new File(
                downloadFolder);
        if (!createDownloadFolder.exists()) {
            if (!createDownloadFolder.mkdir()) {
                logger.error("Failed to create download direction: {}",
                        createDownloadFolder.getAbsolutePath());
                return null;
            }
        }
        return downloadFolder;
    }


}
