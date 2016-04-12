/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 */
package com.bezirk.streaming.threads;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.messagehandler.StreamStatusMessage;
import com.bezirk.sadl.ISadlEventReceiver;
import com.bezirk.sphere.api.IUhuSphereForSadl;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.util.UhuValidatorUtility;

/**
 * This thread is used to send data on the wire. This thread is started by {@link StreamQueueProcessor}. It reads the data from the file, and write into the IP and port. 
 */
public class StreamSendingThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(StreamSendingThread.class);

    private final boolean isSecure;
    private final short localStreamId;
    private final UhuServiceId senderServiceID;
    private final String recipientIP;                                 // recipient
    private final int port;                                           // the port that the recipient is listening
    private final String filePath;                                    // path to the file that has to be sent
    private Socket client;
    private final String sphere;
    private final ISadlEventReceiver sadlReceiver;
	private final IUhuSphereForSadl sphereForSadl;
    private static final int BUFFER_SIZE = 1024;                      // size of the buffer

    public StreamSendingThread(StreamRecord streamRecord,
                               ISadlEventReceiver sadlReceiver,IUhuSphereForSadl sphereForSadl) {
        super();
        this.sphere = streamRecord.Sphere;
        this.recipientIP = streamRecord.recipientIP;
        this.port = streamRecord.recipientPort;
        this.filePath = streamRecord.filePath;
        this.isSecure = streamRecord.isSecure;
        this.localStreamId = streamRecord.localStreamId;
        this.senderServiceID = streamRecord.senderSEP.serviceId;
        this.sadlReceiver = sadlReceiver;
        this.sphereForSadl = sphereForSadl;
    }

    @Override
    public void run() {

        client=null;
        FileInputStream fileInputStream = null;
        DataOutputStream dataOutputStream = null;
        int sentStatus = 1;
        try {
            LOGGER.debug("Thread started to send the data");
            fileInputStream = new FileInputStream(filePath);                              // open the file
            client = new Socket(recipientIP, port);                                       // open the socket
            dataOutputStream = new DataOutputStream(client.getOutputStream());
            if (isSecure) {
            	LOGGER.debug("---------- Secure Data transfer requested! -------------");
            	if(UhuValidatorUtility.isObjectNotNull(sphereForSadl)){
            		sphereForSadl.encryptSphereContent(fileInputStream, dataOutputStream, sphere);
            		LOGGER.debug("---------- Secure Data transfer Completed! -------------");
            	}else{
            		LOGGER.error("SphereForSadl is not initialized. Unable to process secure streaming request.");
            	}
            } else {
            	int noOfBytesReadFromTheFile = 0;
            	byte[] buffer = new byte[BUFFER_SIZE];
            	while ((noOfBytesReadFromTheFile = fileInputStream.read(buffer)) != -1) {
            		dataOutputStream.write(buffer, 0, noOfBytesReadFromTheFile);                           // write into the buffer
            	}
            	LOGGER.debug("---------- Data has been transferred successfully! -------------");
            }
        } catch (FileNotFoundException e) {
            LOGGER.debug("Error in Sending stream : "+filePath, e);
            sentStatus = 0;
        } catch (UnknownHostException e) {
            LOGGER.debug("Error in Opening socket to host : "+recipientIP+" , port : "+port, e);
            sentStatus = 0;
        } catch (IOException e) {
            LOGGER.debug("Error in Sending Thread", e);
            sentStatus = 0;
        } finally {
            closeResources(fileInputStream, dataOutputStream);
        }

        StreamStatusMessage streamStatusMessage = new StreamStatusMessage(
                senderServiceID, sentStatus, localStreamId);
        if (UhuValidatorUtility.isObjectNotNull(sadlReceiver)) {

            sadlReceiver.processStreamStatus(streamStatusMessage);

        } else {

            LOGGER.error("UhuCallback is not provided. Unable to send stream callback.");
        }

        /*// GIVE THE CALLBACK TO THE SERVICE - available in commit 7694fb63003 */

    }

	private void closeResources(FileInputStream fileInputStream,
			DataOutputStream dataOutputStream) {
		try {
		    if (UhuValidatorUtility.isObjectNotNull(dataOutputStream)) {
		        dataOutputStream.flush();
		        dataOutputStream.close();
		    }
		    if (UhuValidatorUtility.isObjectNotNull(fileInputStream)) {
		        fileInputStream.close();
		    }
		    if (UhuValidatorUtility.isObjectNotNull(client)) {
		        client.close();
		    }

		} catch (IOException e) {
		    LOGGER.error("Error in closing the File/ Clients", e);
		}
	}

}
