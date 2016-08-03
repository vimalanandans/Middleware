/**
 * @author Vijet Badigannavar ( bvijet@in.bosch.com )
 */
package com.bezirk.streaming.threads;

import com.bezirk.streaming.MessageQueue;
import com.bezirk.control.messages.Ledger;
import com.bezirk.pubsubbroker.PubSubEventReceiver;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.StreamManager;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.streaming.control.Objects.StreamRecord.StreamRecordStatus;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * This Thread is a blocking and will be iterating on the message queue } to process the {@link StreamRecord}. It checks the {@link StreamRecordStatus} of the
 * {@link StreamRecord} and if it is {@link StreamRecordStatus#READY} it will process further else just remove from the queue.  If the {@link StreamRecordStatus} of the {@link StreamRecord} is
 * {@link StreamRecordStatus#READY} it spawns a {@link StreamSendingThread} and removes the {@link StreamRecord} from the  queue}
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

    private ExecutorService sendStreamExecutor;

    private StreamManager streamManager;

    public StreamQueueProcessor(MessageQueue msgQueue, PubSubEventReceiver sadlReceiver, ExecutorService sendStreamExecutor, StreamManager streamManager) {
        this.sadlReceiver = sadlReceiver;
        this.msgQueue = msgQueue;
        this.sendStreamExecutor = sendStreamExecutor;
        this.streamManager = streamManager;

    }

    public void setSphereSecurity(SphereSecurity sphereSecurity) {
        this.sphereSecurity = sphereSecurity;
    }

    /**
     * This thread is blocking and will be notified when there are any {@link StreamRecord} in the queue. It pops the {@link StreamRecord} from the queue
     * and checks  {@link StreamRecordStatus} of the {@link StreamRecord}.If {@link StreamRecordStatus} is {@link StreamRecordStatus#READY} , it spawns a  {@link StreamSendingThread}.
     * If {@link StreamRecordStatus} is {@link StreamRecordStatus#BUSY} then a notification has to be given back to the zirk via onError() ( yet to be implemented ).
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
            List<Ledger> streamQueue = new CopyOnWriteArrayList<>(
                    msgQueue.getQueue()); // pop the StreamDescriptor record
            Iterator<Ledger> it = streamQueue.iterator();
            if (ValidatorUtility.isObjectNotNull(sadlReceiver)) {

                bezirkCallbackPresent = true;

            } else {

                logger.error("BezirkCallback is not provided. Unable to send stream callback.");
            }

            while (it.hasNext()) {
                StreamRecord streamRecord = (StreamRecord) it.next();

                if (StreamRecordStatus.LOCAL == streamRecord.getStreamRecordStatus()) {
                    processLocalStreamMessage(bezirkCallbackPresent, streamRecord);

                } else if (StreamRecordStatus.ADDRESSED == streamRecord.getStreamRecordStatus()) {
                    logger.debug("StreamDescriptor Request is already Addressed.");
                } else if (streamRecord.getStreamRecordStatus()== StreamRecordStatus.READY) {
                    processStreamReadyMessage(streamRecord);
                } else if (streamRecord.getStreamRecordStatus() == StreamRecordStatus.BUSY) {
                    processStreamBusyMessage(bezirkCallbackPresent, streamRecord);
                }
                msgQueue.removeFromQueue(streamRecord);

                //punith.. remove from active stream map
            }
        }
    }

    private void processStreamBusyMessage(boolean bezirkCallbackPresent,
                                          StreamRecord streamRecord) {

        logger.debug("The Recipient is Busy, Giving Callback to the Zirk");

    /*    StreamStatusAction streamStatusAction = new StreamStatusAction(
                streamRecord.senderSEP.zirkId, 0, streamRecord.localStreamId);

        if (bezirkCallbackPresent) {
            sadlReceiver.processStreamStatus(streamStatusAction);
        }*/
    }

    private void processStreamReadyMessage(StreamRecord streamRecord) {
        if (ValidatorUtility.isObjectNotNull(sphereSecurity)) {
            StreamSendingThread streamSendingThread = new StreamSendingThread(streamRecord, sadlReceiver, sphereSecurity);
            Future streamSendingFuture  = sendStreamExecutor.submit(new Thread(streamSendingThread));
            streamManager.addRefToActiveStream(streamRecord.getStreamRequestKey(), streamSendingFuture);
        } else {
            logger.error("SphereForSadl is not initialized.");
        }
    }

    private void processLocalStreamMessage(boolean bezirkCallbackPresent,
                                           StreamRecord streamRecord) {

        //Punith compare this with the MVP branch, doubt.

        // GIVE THE CALLBACK AS SUCCESS FOR THE SENDER
        /*StreamStatusMessage streamStatusMessage = new StreamStatusMessage(
                streamRecord.getSenderSEP().zirkId, 1, streamRecord.getLocalStreamId());
        if (bezirkCallbackPresent) {

            sadlReceiver.processStreamStatus(streamStatusAction);

        }*/
        // GIVE CALLBACK FOR RECIPIENT
        /*StreamIncomingMessage uStreamCallbackMsg = new StreamIncomingMessage(
                streamRecord.getRecipientSEP().zirkId, streamRecord.getStreamTopic(),
                streamRecord.getSerializedStream(), streamRecord.getFile(),
                streamRecord.getLocalStreamId(), streamRecord.getSenderSEP());
        if (bezirkCallbackPresent) {
            sadlReceiver.processNewStream(uStreamCallbackMsg);
        }
        */
    }

}
