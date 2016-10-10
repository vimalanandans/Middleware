/**
 *
 */
package com.bezirk.middleware.core.streaming;


import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.Ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


/**
 * There are two running instances of type MessageQueue.
 * SendingMessageQueue: is the queue on the sender side which is populated by services using the
 * BezirkProxy and processed by the SenderThread
 * ReceivingMessageQueue: is the queue on the receiver side which is populated by the
 * BeirkCommsMulticastListener and BezirkCommsUnicastListener. The queue is processed by the
 * ReceiverThread
 */
public class MessageQueue {
    private static final Logger logger = LoggerFactory.getLogger(MessageQueue.class);
    private static final int MAX_SIZE = 1000;
    private final ArrayList<ControlMessage> queue = new ArrayList<>();

    /**
     * @param message to be added to the queue
     * @see Ledger
     */
    public void addToQueue(ControlMessage message) {
        synchronized (this) {
            // wait if the queue is full
            while (queue.size() == MAX_SIZE) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error("Interrupted while waiting when the queue was full ", e);
                    Thread.currentThread().interrupt();
                }
            }

            if (queue.contains(message)) {
                logger.warn("message w/ already present in sender queue");
            } else {
                queue.add(message);
                notifyAll();
            }
        }

    }


    /**
     * @param message to be removed
     * @see Ledger
     */
    public void removeFromQueue(ControlMessage message) {
        synchronized (this) {
            if (queue.isEmpty()) return;

            if (queue.remove(message)) {
                notifyAll();
            } else {
                logger.warn("message already removed from sender queue");
            }
        }
    }

    /**
     * @return the MessageQueue which contains messages of type PackagedMessage
     * @see Ledger
     */
    public ArrayList<ControlMessage> getQueue() {
        synchronized (this) {
            while (queue.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.warn("Send/Receive Queue Interrupted");
                    Thread.currentThread().interrupt();
                }
            }
            return queue;
        }
    }
}
