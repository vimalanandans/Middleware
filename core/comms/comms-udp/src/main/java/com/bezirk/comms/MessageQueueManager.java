/**
 *
 */
package com.bezirk.comms;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 *         The MessageQueueManager maintains the running instances of the sendingMessageQueue and receivingMessageQueue.
 *         Operations on the respective queues are invoked using the get operations on the MessageQueueManager
 * @see MessageQueue
 */

@Deprecated
// No Global Queue manager. functionalities  are moved to BezirkCommsManager// Delete this file later
public class MessageQueueManager {
    //private final static String TAG = MessageQueueManager.class.getCanonicalName();
    private static final Logger log = LoggerFactory.getLogger(MessageQueueManager.class);

    private static MessageQueue sendingMessageQueue;
    private static MessageQueue receiverMessageQueue;
    private static MessageQueue controlSenderQueue;
    private static MessageQueue controlReceiverQueue;
    private static MessageQueue streamingMessageQueue;

    /**
     * @return the sending message queue
     * @see MessageQueue
     */
    private MessageQueue getSendingMessageQueue() {
        return sendingMessageQueue;
    }

    /**
     * This method is invoked as soon as instance of messagequeue for the sender side of the stack is created. This is part of the stack init() method
     *
     * @param sendingMsgQueue the instance of type MessageQueue which is used on the sender side
     * @see MessageQueue
     */
    private void setSendingMessageQueue(MessageQueue sendingMsgQueue) {
        log.info("Bezirk Sending Message Queue has been initialized\n");
        MessageQueueManager.sendingMessageQueue = sendingMsgQueue;
    }

    /**
     * @return the receiver message queue
     * @see MessageQueue
     */
    private MessageQueue getReceiverMessageQueue() {
        return receiverMessageQueue;
    }

    /**
     * This method is invoked as soon as instance of messagequeue for the receiver side of the stack is created. This is part of the stack init() method
     *
     * @param receiverMessageQueue the instance of type MessageQueue which is used on the sender side
     * @see MessageQueue
     */
    private void setReceiverMessageQueue(MessageQueue receiverMessageQueue) {
        log.info("Bezirk Receiver Message Queue has been initialized\n");
        MessageQueueManager.receiverMessageQueue = receiverMessageQueue;
    }

    /**
     * This is the message queue for control messages on the sending side
     *
     * @return {@link MessageQueue}
     */
    private MessageQueue getControlSenderQueue() {
        return controlSenderQueue;
    }

    /**
     * This is the message queue for control messages on the sending side
     *
     * @param controlSenderQueue {@link MessageQueue}
     */
    private void setControlSenderQueue(MessageQueue controlSenderQueue) {
        log.info("Control Sender Queue has been initialized");
        MessageQueueManager.controlSenderQueue = controlSenderQueue;
    }

    /**
     * This is the message queue for control messages on the receiver side
     *
     * @return {@link MessageQueue}
     */
    private MessageQueue getControlReceiverQueue() {
        return controlReceiverQueue;
    }

    /**
     * This is the message queue for control messages on the receiver side
     *
     * @param controlReceiverQueue
     */
    private void setControlReceiverQueue(MessageQueue controlReceiverQueue) {
        log.info("Control Receiver Queue has been initialized");
        MessageQueueManager.controlReceiverQueue = controlReceiverQueue;
    }

    /**
     * This is the message queue for stream requests on the receiver side
     *
     * @return {@link MessageQueue}
     */
    private MessageQueue getStreamingMessageQueue() {
        return streamingMessageQueue;
    }

    /**
     * This is the message queue for stream requests  on the sender side
     *
     * @param streamingMessageQueue
     */
    private void setStreamingMessageQueue(MessageQueue streamingMessageQueue) {
        log.info("Streaming Message Queue has been initialized");
        MessageQueueManager.streamingMessageQueue = streamingMessageQueue;
    }
}
