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

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.middleware.core.util.ValidatorUtility;
import com.bezirk.streaming.StreamRecord;

/**
 * This is FileStreamSenderThread, Which will be responsible for sending the chunks of bytes on the open Socket
 * of {@link com.bezirk.streaming.receiver.FileStreamReceivingThread}
 *
 */

public class FileStreamSenderThread implements Callable<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(FileStreamSenderThread.class);
    private static final int BUFFER_SIZE = 1024;

    private Socket client;
    private final StreamRecord streamRecord;


    public FileStreamSenderThread(StreamRecord streamRecord){
        this.streamRecord  = streamRecord;
    }

    @Override
    public Boolean call() {
        Boolean streamStatus = Boolean.FALSE;
        client = null;
        FileInputStream fileInputStream = null;
        DataOutputStream dataOutputStream = null;
        try {
            logger.debug("Thread started to send the data for file {} and stream id {}", streamRecord.getFile().getName(), streamRecord.getStreamId());
            fileInputStream = new FileInputStream(streamRecord.getFile());
            client = new Socket(streamRecord.getRecipientIp(), streamRecord.getRecipientPort());
            dataOutputStream = new DataOutputStream(client.getOutputStream());

            int noOfBytesReadFromTheFile;
            final byte[] buffer = new byte[BUFFER_SIZE];
            while ((noOfBytesReadFromTheFile = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, noOfBytesReadFromTheFile);
            }
            logger.debug("---------- Data has been transferred successfully! -------------");
            streamStatus = Boolean.TRUE;
        } catch (FileNotFoundException e) {
            logger.debug("Error in Sending stream : {}",streamRecord.getFile().getPath(), e);
        } catch (UnknownHostException e) {
            logger.debug("Error in Opening socket to host : {}, for port {}", streamRecord.getRecipientIp(), streamRecord.getRecipientPort(), e);
        } catch (IOException e) {
            logger.debug("Error in Sending Thread", e);
        } finally {
            closeResources(fileInputStream, dataOutputStream);
        }

        return streamStatus;
    }

    /**
     * gracefully close all the open resources
     * @param fileInputStream input stream resource instance.
     * @param dataOutputStream Output stream resource instance.
     */
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
