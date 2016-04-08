/**
 * @author Vijet Badigannavar ( bvijet@in.bosch.com )
 */
package com.bosch.upa.uhu.streaming.threads;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.messagehandler.StreamStatusMessage;
import com.bosch.upa.uhu.messagehandler.StreamIncomingMessage;
import com.bosch.upa.uhu.comms.MessageQueue;
import com.bosch.upa.uhu.control.messages.Ledger;
import com.bosch.upa.uhu.sadl.ISadlEventReceiver;
import com.bosch.upa.uhu.sphere.api.IUhuSphereForSadl;
import com.bosch.upa.uhu.streaming.control.Objects.StreamRecord;
import com.bosch.upa.uhu.streaming.control.Objects.StreamRecord.StreamingStatus;
import com.bosch.upa.uhu.util.UhuValidatorUtility;

/**
 * This Thread is a blocking and will be iterating on the message queue } to process the {@link StreamRecord}. It checks the {@link StreamingStatus} of the
 * {@link StreamRecord} and if it is {@link StreamingStatus#READY} it will process further else just remove from the queue.  If the {@link StreamingStatus} of the {@link StreamRecord} is 
 * {@link StreamingStatus#READY} it spawns a {@link StreamSendingThread} and removes the {@link StreamRecord} from the  queue}
 * 
 * TODO : onError has to be discussed!
 * @see com.bosch.upa.uhu.streaming.control.Objects.StreamRecord
 *
 */
public class StreamQueueProcessor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamQueueProcessor.class);

    private final MessageQueue msgQueue;

    private final ISadlEventReceiver sadlReceiver;

    private IUhuSphereForSadl sphereForSadl;

    public StreamQueueProcessor(MessageQueue msgQueue, ISadlEventReceiver sadlReceiver) {
        this.sadlReceiver = sadlReceiver;
        this.msgQueue = msgQueue;

    }

    public void setSphereForSadl(IUhuSphereForSadl sphereForSadl) {
        this.sphereForSadl = sphereForSadl;
    }

    /**
     * This thread is blocking and will be notified when there are any {@link StreamRecord} in the queue. It pops the {@link StreamRecord} from the queue
     * and checks  {@link StreamingStatus} of the {@link StreamRecord}.If {@link StreamingStatus} is {@link StreamingStatus#READY} , it spawns a  {@link StreamSendingThread}.
     * If {@link StreamingStatus} is {@link StreamingStatus#BUSY} then a notification has to be given back to the service via onError() ( yet to be implemented ).
     * The {@link StreamRecord} is removed from the stream queue}
     * @see java.lang.Runnable#run()
     */ 
    @Override
    public void run() {
       boolean running = true;
        LOGGER.debug("Uhu Stream Processor has started");
        boolean uhuCallbackPresent = false;
        while (running) {
            if (Thread.currentThread().isInterrupted()) {
                LOGGER.debug("Stopping Uhu Stream Processor Thread");
                running = false;
                continue;
            }
            List<Ledger> streamQueue = new CopyOnWriteArrayList<Ledger>(
                    msgQueue.getQueue()); // pop the Stream record
            Iterator<Ledger> it = streamQueue.iterator();
            if (UhuValidatorUtility.isObjectNotNull(sadlReceiver)) {

                uhuCallbackPresent = true;

            } else {

                LOGGER.error("UhuCallback is not provided. Unable to send stream callback.");
            }

            while (it.hasNext()) {
                StreamRecord streamRecord = (StreamRecord) it.next();

                if (StreamingStatus.LOCAL == streamRecord.streamStatus) {
                    processLocalStreamMessage(uhuCallbackPresent, streamRecord);

                } else if (StreamingStatus.ADDRESSED == streamRecord.streamStatus) {
                    LOGGER.debug("Stream Request is already Addressed.");
                } else if (streamRecord.streamStatus == StreamingStatus.READY) {
                    processStreamReadyMessage(streamRecord);
                } else if (streamRecord.streamStatus == StreamingStatus.BUSY) {
                    processStreamBusyMessage(uhuCallbackPresent, streamRecord);
                }
                msgQueue.removeFromQueue(streamRecord);
            }
        }
    }

    private void processStreamBusyMessage(boolean uhuCallbackPresent,
            StreamRecord streamRecord) {

        LOGGER.debug("The Receipient is Busy, Giving Callback to the Service");

        StreamStatusMessage streamStatusMessage = new StreamStatusMessage(
                streamRecord.senderSEP.serviceId, 0, streamRecord.localStreamId);

        if (uhuCallbackPresent) {
            sadlReceiver.processStreamStatus(streamStatusMessage);
        }
    }

    private void processStreamReadyMessage(StreamRecord streamRecord) {
        if (streamRecord.isIncremental 
                || streamRecord.allowDrops) {
        	
        	LOGGER.debug("Uhu Supports only RELIABLE-COMPLETE..as of now..");
           
        } else {
            
        	 if (UhuValidatorUtility.isObjectNotNull(sphereForSadl)) {
        		 
                 new Thread(new StreamSendingThread(streamRecord, sadlReceiver, sphereForSadl)).start();                       // spawn the thread
             } else {

                 LOGGER.error("SphereForSadl is not initialized.");
             }
        }
    }

    private void processLocalStreamMessage(boolean uhuCallbackPresent,
            StreamRecord streamRecord) {
        // GIVE THE CALLBACK AS SUCCESS FOR THE SENDER
        StreamStatusMessage streamStatusMessage = new StreamStatusMessage(
                streamRecord.senderSEP.serviceId, 1, streamRecord.localStreamId);
        if (uhuCallbackPresent) {

            sadlReceiver.processStreamStatus(streamStatusMessage);

        }
        // GIVE CALLBACK FOR RECIPIENT
        StreamIncomingMessage uStreamCallbackMsg = new StreamIncomingMessage(
                streamRecord.recipientSEP.serviceId, streamRecord.streamTopic,
                streamRecord.serializedStream, streamRecord.filePath,
                streamRecord.localStreamId, streamRecord.senderSEP);

        if (uhuCallbackPresent) {
            sadlReceiver.processNewStream(uStreamCallbackMsg);
        }
    }

}
