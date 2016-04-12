/**
 * 
 */
package com.bezirk.comms;


import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.control.messages.Ledger;


/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 * There are two running instances of type MessageQueue. 
 * SendingMessageQueue: is the queue on the sender side which is populated by services using the UhuProxy and processed by the SenderThread
 * ReceivingMessageQueue: is the queue on the receiver side which is populated by the UhuCommsMulticastListener and UhuCommsUnicastListener. The queue is processed by the ReceiverThread
 * @see UhuProxyForServiceAPI
 * @see EventMulticastListener
 * @see EventUnicastListener
 */
public class MessageQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageQueue.class);

    private final ArrayList<Ledger> queue = new ArrayList<Ledger>();
	private final static int MaxSize=1000;

	/**
	 *
	 * @param message to be added to the queue
	 * @see Ledger
	 */
	public void addToQueue(Ledger message) {
		synchronized (this) {
			// wait if the queue is full
			while (queue.size() == MaxSize) {
				try {
					wait();
				} catch (InterruptedException e) {
					LOGGER.error(
							"Interrupted while waiting when the queue was full ",
							e);
				}
			}
			if (queue.contains(message)) {

				LOGGER.warn("message w/ already present in sender queue");

			} else {

				queue.add(message);
				notifyAll();
			}
		}

	}


	/**
	 * 
	 * @param message to be removed 
	 * @see Ledger
	 */
	public void removeFromQueue(Ledger message){
		synchronized (this) {
			if (queue.isEmpty()) {
				return;
			}
			if (queue.contains(message)) {
				queue.remove(message);
				notifyAll();
			} else {

				LOGGER.warn("message already removed from sender queue");

			}
		}
	}

	/**
	 * 
	 * @return the MessageQueue which contains messages of type PackagedMessage
	 * @see Ledger
	 */
	public ArrayList<Ledger> getQueue(){
		synchronized (this) {
			while (queue.isEmpty()) {
				try {
					wait();
				} catch (InterruptedException e) {
					LOGGER.warn("Send/Receive Queue Interrupted");
				}
			}
			return queue;
		}
	}

	public int getMaxsize() {
		return MaxSize;
	}

}
