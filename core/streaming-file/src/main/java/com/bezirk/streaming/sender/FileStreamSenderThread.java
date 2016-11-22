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
package com.bezirk.streaming.sender;

import com.bezirk.middleware.core.util.ValidatorUtility;
import com.bezirk.streaming.StreamRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by PIK6KOR on 11/15/2016.
 */

public class FileStreamSenderThread implements Runnable {

    private Socket client;
    private static final Logger logger = LoggerFactory
            .getLogger(FileStreamSenderThread.class);
    private static final int BUFFER_SIZE = 1024; // size of the buffer
    private final File file;
    private final String recipientIP;                                 // recipient
    private final int port;                                           // the port that the recipient is listening

    public FileStreamSenderThread(StreamRecord streamRecord){
        this.file = streamRecord.getFile();
        this.recipientIP = streamRecord.getRecipientIp();
        this.port = streamRecord.getRecipientPort();
    }

    @Override
    public void run() {
        client = null;
        FileInputStream fileInputStream = null;
        DataOutputStream dataOutputStream = null;
        try {
            logger.debug("Thread started to send the data");
            fileInputStream = new FileInputStream(file);  // open the file
            client = new Socket(recipientIP, port);       // open the socket
            dataOutputStream = new DataOutputStream(client.getOutputStream());

            int noOfBytesReadFromTheFile;
            final byte[] buffer = new byte[BUFFER_SIZE];
            while ((noOfBytesReadFromTheFile = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, noOfBytesReadFromTheFile);   // write into the buffer
            }
            logger.debug("---------- Data has been transferred successfully! -------------");
        } catch (FileNotFoundException e) {
            logger.debug("Error in Sending stream : " + file.getPath(), e);
        } catch (UnknownHostException e) {
            logger.debug("Error in Opening socket to host : " + recipientIP + " , port : " + port, e);
        } catch (IOException e) {
            logger.debug("Error in Sending Thread", e);
        } finally {
            closeResources(fileInputStream, dataOutputStream);
        }
    }

    private void closeResources(FileInputStream fileInputStream,
                                DataOutputStream dataOutputStream) {
        try {
            if (ValidatorUtility.isObjectNotNull(dataOutputStream)) {
                dataOutputStream.flush();
                dataOutputStream.close();
            }
            if (ValidatorUtility.isObjectNotNull(fileInputStream)) {
                fileInputStream.close();
            }
            if (ValidatorUtility.isObjectNotNull(client)) {
                client.close();
            }

        } catch (IOException e) {
            logger.error("Error in closing the File/ Clients", e);
        }
    }
}
