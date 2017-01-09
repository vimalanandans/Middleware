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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.middleware.core.util.ValidatorUtility;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;

/**
 * This thread will be started by the File receiver bezirk instance. Once bezirk instance will
 * receive a {@link com.bezirk.streaming.StreamRecord} with
 * {@link com.bezirk.streaming.StreamRecord.StreamRecordStatus#ALIVE} state it starts this thread
 * at assigned port and waits for the socket connection.
 *
 */

class FileStreamReceivingThread implements Callable<String>{

    private static final int CONNECTION_TIMEOUT_TIME = 45000;
    private static final String FILE_DOWNLOAD_FOLDER = "/storage/emulated/0/bezirk/downloads/";
    private static final int BUFFER_SIZE = 1024;
    private static final Logger logger = LoggerFactory.getLogger(FileStreamReceivingThread.class);

    private Integer assignedPort = -1;
    private final File file;
    private final FileStreamPortFactory portFactory;

    FileStreamReceivingThread(Integer port, File file, FileStreamPortFactory portFactory){
        this.assignedPort = port;
        this.file = file;
        this.portFactory = portFactory;
    }

    @Override
    public String call() {
        ServerSocket socket = null;
        Socket receivingSocket = null;
        File tempFile = null;
        FileOutputStream fileOutputStream = null;
        DataInputStream inputStream = null;
        final String downloadedFile = FILE_DOWNLOAD_FOLDER + file.getName();
        boolean streamErrored = true;

        try {
            socket = new ServerSocket(assignedPort);
            socket.setSoTimeout(CONNECTION_TIMEOUT_TIME);
            receivingSocket = socket.accept();

            if(validateDownloadPath(FILE_DOWNLOAD_FOLDER)){
                tempFile = new File(downloadedFile);
                fileOutputStream = new FileOutputStream(tempFile);
                inputStream = new DataInputStream(receivingSocket.getInputStream());

                int noOfBytesRead;
                final byte[] buffer = new byte[BUFFER_SIZE];
                logger.debug("Started file receiving thread for file {}", file.getName());
                while ((noOfBytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, noOfBytesRead);
                }

                streamErrored = false;
            }else{
                logger.error("Failed to create folder!");
            }
        } catch (SocketException e) {
            logger.error("Connection Timeout, the client didn't connect within specified timeout", e);
        } catch (FileNotFoundException e) {
            logger.error("Unable to receiving stream file in downloads.", e);
        } catch (IOException e) {
            logger.error("Exception occurred while receiving stream.", e);
        } finally {
            portFactory.releasePort(assignedPort);
            closeResources(socket, receivingSocket, fileOutputStream, inputStream);
            if (streamErrored) {
                if (tempFile!= null && tempFile.exists() && !tempFile.delete()) {
                    logger.error("Failed to delete temporary stream file: {}", tempFile);
                }
                return null;
            }

        }
        return downloadedFile;
    }

    /**
     * close the streaming resources.
     * @param socket open socket
     * @param receivingSocket receiving socket
     * @param fileOutputStream stream of bytes which will be written to the file being saved.
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
            if (ValidatorUtility.isObjectNotNull(receivingSocket)) {
                receivingSocket.close();
            }
            if (ValidatorUtility.isObjectNotNull(socket)) {
                socket.close();
            }
        } catch (IOException e) {

            logger.error("Exception in closing resources.", e);
        }
    }

    /**
     * creates the folder if not existing and returns the download path for the respective
     * environment. Will return null if the createDownloadFolder.mkdir() return false.
     * @return downloadFolder Path of the download folder.
     */
    private boolean validateDownloadPath(String downloadFolder){
        final File createDownloadFolder = new File(downloadFolder);
        boolean validatePath;
        if (createDownloadFolder.exists()) {
            validatePath = true;
            logger.debug("Download folder {} already exist", createDownloadFolder.getAbsolutePath());
        }else{
            if (createDownloadFolder.mkdir()) {
                validatePath = true;
                logger.debug("create download folder: {}", createDownloadFolder.getAbsolutePath());
            }else{
                logger.error("Failed to create download directory: {}",
                        createDownloadFolder.getAbsolutePath());
                validatePath = false;
            }
        }
        return validatePath;
    }


}
