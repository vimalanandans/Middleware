/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 */
package com.bezirk.streaming.threads;

import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.pubsubbroker.PubSubEventReceiver;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.util.ValidatorUtility;

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
 * This thread is used to send data on the wire. This thread is started by {@link StreamQueueProcessor}. It reads the data from the file, and write into the IP and port.
 */
public class StreamSendingThread implements Runnable {
    private static final Logger logger = LoggerFactory
            .getLogger(StreamSendingThread.class);
    private static final int BUFFER_SIZE = 1024;                      // size of the buffer
    private final boolean isEncrypted;
    private final ZirkId senderZirkID;
    private final String recipientIP;                                 // recipient
    private final int port;                                           // the port that the recipient is listening
    private final File file;                                    // path to the file that has to be sent
    private final String sphere;
    private final PubSubEventReceiver sadlReceiver;
    private final SphereSecurity sphereSecurity;
    private Socket client;

    public StreamSendingThread(StreamRecord streamRecord,
                               PubSubEventReceiver sadlReceiver, SphereSecurity sphereSecurity) {
        super();
        this.sphere = streamRecord.sphere;
        this.recipientIP = streamRecord.recipientIP;
        this.port = streamRecord.recipientPort;
        this.file = streamRecord.file;
        this.isEncrypted = streamRecord.isEncrypted;
        this.senderZirkID = streamRecord.senderSEP.zirkId;
        this.sadlReceiver = sadlReceiver;
        this.sphereSecurity = sphereSecurity;
    }

    @Override
    public void run() {
        client = null;
        FileInputStream fileInputStream = null;
        DataOutputStream dataOutputStream = null;
        try {
            logger.debug("Thread started to send the data");
            fileInputStream = new FileInputStream(file);                              // open the file
            client = new Socket(recipientIP, port);                                       // open the socket
            dataOutputStream = new DataOutputStream(client.getOutputStream());

            if (isEncrypted && sphereSecurity != null ) // encrypted and valid sphere security object
            {
                logger.debug("---------- Secure Data transfer requested! -------------");

                    sphereSecurity.encryptSphereContent(fileInputStream, dataOutputStream, sphere);
                    logger.debug("---------- Secure Data transfer Completed! -------------");

            } else {
                int noOfBytesReadFromTheFile;
                final byte[] buffer = new byte[BUFFER_SIZE];
                while ((noOfBytesReadFromTheFile = fileInputStream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, noOfBytesReadFromTheFile);                           // write into the buffer
                }
                logger.debug("---------- Data has been transferred successfully! -------------");
            }
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
