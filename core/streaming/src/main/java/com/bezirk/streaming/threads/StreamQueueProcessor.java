/**
 * @author Vijet Badigannavar ( bvijet@in.bosch.com )
 */
package com.bezirk.streaming.threads;

import com.bezirk.actions.StreamStatusAction;
import com.bezirk.streaming.MessageQueue;
import com.bezirk.control.messages.Ledger;
import com.bezirk.actions.ReceiveFileStreamAction;
import com.bezirk.pubsubbroker.PubSubEventReceiver;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.streaming.control.Objects.StreamRecord.StreamingStatus;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This Thread is a blocking and will be iterating on the message queue } to process the {@link StreamRecord}. It checks the {@link StreamingStatus} of the
 * {@link StreamRecord} and if it is {@link StreamingStatus#READY} it will process further else just remove from the queue.  If the {@link StreamingStatus} of the {@link StreamRecord} is
 * {@link StreamingStatus#READY} it spawns a {@link StreamSendingThread} and removes the {@link StreamRecord} from the  queue}
 * <p>
 * TODO : onError has to be discussed!
 * </p>
 *
 * @see StreamRecord
 */
public class StreamQueueProcessor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(StreamQueueProcessor.class);

    private final MessageQueue msgQueue;

    private final PubSubEventReceiver sadlReceiver;

    private SphereSecurity sphereSecurity;

    public StreamQueueProcessor(MessageQueue msgQueue, PubSubEventReceiver sadlReceiver) {
        this.sadlReceiver = sadlReceiver;
        this.msgQueue = msgQueue;

    }

    public void setSphereSecurity(SphereSecurity sphereSecurity) {
        this.sphereSecurity = sphereSecurity;
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
        logger.debug("Bezirk StreamDescriptor Processor has started");
        boolean bezirkCallbackPresent = false;
        while (running) {
            if (Thread.currentThread().isInterrupted()) {
                logger.debug("Stopping Bezirk StreamDescriptor Processor Thread");
                running = false;
                continue;
            }
            List<Ledger> streamQueue = new CopyOnWriteArrayList<Ledger>(
                    msgQueue.getQueue()); // pop the StreamDescriptor record
            Iterator<Ledger> it = streamQueue.iterator();
            if (ValidatorUtility.isObjectNotNull(sadlReceiver)) {

                bezirkCallbackPresent = true;

            } else {

                logger.error("BezirkCallback is not provided. Unable to send stream callback.");
            }

            while (it.hasNext()) {
                StreamRecord streamRecord = (StreamRecord) it.next();

                if (StreamingStatus.LOCAL == streamRecord.streamStatus) {
                    processLocalStreamMessage(bezirkCallbackPresent, streamRecord);

                } else if (StreamingStatus.ADDRESSED == streamRecord.streamStatus) {
                    logger.debug("StreamDescriptor Request is already Addressed.");
                } else if (streamRecord.streamStatus == StreamingStatus.READY) {
                    processStreamReadyMessage(streamRecord);
                } else if (streamRecord.streamStatus == StreamingStatus.BUSY) {
                    processStreamBusyMessage(bezirkCallbackPresent, streamRecord);
                }
                msgQueue.removeFromQueue(streamRecord);
            }
        }
    }

    private void processStreamBusyMessage(boolean bezirkCallbackPresent,
                                          StreamRecord streamRecord) {

        logger.debug("The Recipient is Busy, Giving Callback to the Zirk");

        StreamStatusAction streamStatusAction = new StreamStatusAction(
                streamRecord.senderSEP.zirkId, 0, streamRecord.localStreamId);

        if (bezirkCallbackPresent) {
            sadlReceiver.processStreamStatus(streamStatusAction);
        }
    }

    private void processStreamReadyMessage(StreamRecord streamRecord) {
        if (streamRecord.isIncremental
                || streamRecord.isReliable) {

            logger.debug("Bezirk Supports only RELIABLE-COMPLETE..as of now..");

        } else {

                new Thread(new StreamSendingThread(streamRecord, sadlReceiver, sphereSecurity)).start();                       // spawn the thread

        }
    }

    private void processLocalStreamMessage(boolean bezirkCallbackPresent,
                                           StreamRecord streamRecord) {
        // GIVE THE CALLBACK AS SUCCESS FOR THE SENDER
        StreamStatusAction streamStatusAction = new StreamStatusAction(
                streamRecord.senderSEP.zirkId, 1, streamRecord.localStreamId);
        if (bezirkCallbackPresent) {

            sadlReceiver.processStreamStatus(streamStatusAction);

        }
        // GIVE CALLBACK FOR RECIPIENT
        ReceiveFileStreamAction uStreamCallbackMsg = new ReceiveFileStreamAction(
                streamRecord.recipientSEP.zirkId,
                streamRecord.serializedStream, streamRecord.file,
                streamRecord.localStreamId, streamRecord.senderSEP);

        if (bezirkCallbackPresent) {
            sadlReceiver.processNewStream(uStreamCallbackMsg);
        }
    }

}
