/**
 * @author Vijet Badigannavar ( bvijet@in.bosch.com )
 */
package com.bezirk.streaming.threads;

import com.bezirk.comms.MessageQueue;
import com.bezirk.control.messages.Ledger;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.messagehandler.StreamStatusMessage;
import com.bezirk.sadl.SadlEventReceiver;
import com.bezirk.sphere.api.BezirkSphereForSadl;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.streaming.control.Objects.StreamRecord.StreamingStatus;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This Thread is a blocking and will be iterating on the message queue } to process the {@link StreamRecord}. It checks the {@link StreamingStatus} of the
 * {@link StreamRecord} and if it is {@link StreamingStatus#READY} it will process further else just remove from the queue.  If the {@link StreamingStatus} of the {@link StreamRecord} is
 * {@link StreamingStatus#READY} it spawns a {@link StreamSendingThread} and removes the {@link StreamRecord} from the  queue}
 * <p/>
 * TODO : onError has to be discussed!
 *
 * @see StreamRecord
 */
public class StreamQueueProcessor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(StreamQueueProcessor.class);

    private final MessageQueue msgQueue;

    private final SadlEventReceiver sadlReceiver;

    private BezirkSphereForSadl sphereForSadl;

    public StreamQueueProcessor(MessageQueue msgQueue, SadlEventReceiver sadlReceiver) {
        this.sadlReceiver = sadlReceiver;
        this.msgQueue = msgQueue;

    }

    public void setSphereForSadl(BezirkSphereForSadl sphereForSadl) {
        this.sphereForSadl = sphereForSadl;
    }

    /**
     * This thread is blocking and will be notified when there are any {@link StreamRecord} in the queue. It pops the {@link StreamRecord} from the queue
     * and checks  {@link StreamingStatus} of the {@link StreamRecord}.If {@link StreamingStatus} is {@link StreamingStatus#READY} , it spawns a  {@link StreamSendingThread}.
     * If {@link StreamingStatus} is {@link StreamingStatus#BUSY} then a notification has to be given back to the zirk via onError() ( yet to be implemented ).
     * The {@link StreamRecord} is removed from the stream queue}
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        boolean running = true;
        logger.debug("Bezirk Stream Processor has started");
        boolean uhuCallbackPresent = false;
        while (running) {
            if (Thread.currentThread().isInterrupted()) {
                logger.debug("Stopping Bezirk Stream Processor Thread");
                running = false;
                continue;
            }
            List<Ledger> streamQueue = new CopyOnWriteArrayList<Ledger>(
                    msgQueue.getQueue()); // pop the Stream record
            Iterator<Ledger> it = streamQueue.iterator();
            if (BezirkValidatorUtility.isObjectNotNull(sadlReceiver)) {

                uhuCallbackPresent = true;

            } else {

                logger.error("UhuCallback is not provided. Unable to send stream callback.");
            }

            while (it.hasNext()) {
                StreamRecord streamRecord = (StreamRecord) it.next();

                if (StreamingStatus.LOCAL == streamRecord.streamStatus) {
                    processLocalStreamMessage(uhuCallbackPresent, streamRecord);

                } else if (StreamingStatus.ADDRESSED == streamRecord.streamStatus) {
                    logger.debug("Stream Request is already Addressed.");
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

        logger.debug("The Recipient is Busy, Giving Callback to the Zirk");

        StreamStatusMessage streamStatusMessage = new StreamStatusMessage(
                streamRecord.senderSEP.zirkId, 0, streamRecord.localStreamId);

        if (uhuCallbackPresent) {
            sadlReceiver.processStreamStatus(streamStatusMessage);
        }
    }

    private void processStreamReadyMessage(StreamRecord streamRecord) {
        if (streamRecord.isIncremental
                || streamRecord.isReliable) {

            logger.debug("Bezirk Supports only RELIABLE-COMPLETE..as of now..");

        } else {

            if (BezirkValidatorUtility.isObjectNotNull(sphereForSadl)) {

                new Thread(new StreamSendingThread(streamRecord, sadlReceiver, sphereForSadl)).start();                       // spawn the thread
            } else {

                logger.error("SphereForSadl is not initialized.");
            }
        }
    }

    private void processLocalStreamMessage(boolean uhuCallbackPresent,
                                           StreamRecord streamRecord) {
        // GIVE THE CALLBACK AS SUCCESS FOR THE SENDER
        StreamStatusMessage streamStatusMessage = new StreamStatusMessage(
                streamRecord.senderSEP.zirkId, 1, streamRecord.localStreamId);
        if (uhuCallbackPresent) {

            sadlReceiver.processStreamStatus(streamStatusMessage);

        }
        // GIVE CALLBACK FOR RECIPIENT
        StreamIncomingMessage uStreamCallbackMsg = new StreamIncomingMessage(
                streamRecord.recipientSEP.zirkId, streamRecord.streamTopic,
                streamRecord.serializedStream, streamRecord.file,
                streamRecord.localStreamId, streamRecord.senderSEP);

        if (uhuCallbackPresent) {
            sadlReceiver.processNewStream(uStreamCallbackMsg);
        }
    }

}
