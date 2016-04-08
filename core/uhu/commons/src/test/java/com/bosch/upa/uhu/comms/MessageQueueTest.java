package com.bosch.upa.uhu.comms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.control.messages.EventLedger;
import com.bosch.upa.uhu.control.messages.Ledger;

/**
 * This testcase verifies the working of message queue by adding messages to the queue and checking queue size.
 * 
 * @author ajc6kor
 *
 */
public class MessageQueueTest {

	@Test
	public void test() {

		MessageQueue messageQueue = new MessageQueue();
		Ledger message = new EventLedger();
		messageQueue.addToQueue(message);
		
		assertEquals("MessageQueue Maxsize is not equal to the default value",1000, messageQueue.getMaxsize());

		assertEquals("MessageQueue size is not equal to the number of messages in queue.",1, messageQueue.getQueue().size());
		
		assertTrue("Message added to queue is not present in the queue.", messageQueue.getQueue().contains(message));
	
		messageQueue.removeFromQueue(message);
		
		Ledger ctrlMessage = new ControlLedger();
		messageQueue.addToQueue(ctrlMessage);
		
		assertFalse("Message removed from queue is present in the queue.", messageQueue.getQueue().contains(message));

		
	}

}
