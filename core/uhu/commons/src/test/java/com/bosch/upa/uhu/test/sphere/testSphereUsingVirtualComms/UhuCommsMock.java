package com.bosch.upa.uhu.test.sphere.testSphereUsingVirtualComms;

import java.net.InetAddress;
import java.util.ArrayList;

import com.bosch.upa.uhu.comms.CommsProperties;
import com.bosch.upa.uhu.comms.ICommsNotification ;
import com.bosch.upa.uhu.comms.ICtrlMsgReceiver;
import com.bosch.upa.uhu.comms.IPortFactory;
import com.bosch.upa.uhu.comms.IUhuCommsLegacy;
import com.bosch.upa.uhu.comms.MessageQueue;
import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.control.messages.ControlMessage.Discriminator;
import com.bosch.upa.uhu.control.messages.Ledger;
import com.bosch.upa.uhu.pipe.core.PipeManager;
import com.bosch.upa.uhu.sadl.UhuSadlManager;
import com.bosch.upa.uhu.sphere.api.IUhuSphereForSadl;
import com.bosch.upa.uhu.streaming.control.Objects.StreamRecord;

@SuppressWarnings("deprecation")
public class UhuCommsMock implements IUhuCommsLegacy{
	
	//The message (request/response) that was received/sent by a device.
	public ControlLedger message;

	@Override
	public boolean startComms() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopComms() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean closeComms() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * We will actually not be sending out the message. 
	 * The message sent/received by a device is stored and processed in the appropriate device.
	 */
	@Override
	public boolean sendMessage(Ledger ledgerMessage) {
		message = (ControlLedger) ledgerMessage;
		return true;
	}

	@Override
	public boolean sendStream(String uniqueKey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean registerStreamBook(String key, StreamRecord sRecord) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean registerNotification(
			ICommsNotification errNotificationCallback) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initComms(CommsProperties commsProperties, InetAddress addr,
			UhuSadlManager sadl, PipeManager pipe) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean registerControlMessageReceiver(Discriminator id,
			ICtrlMsgReceiver receiver) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSphereForSadl(IUhuSphereForSadl uhuSphere) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setReceiverQueues(MessageQueue eventQueue,
			MessageQueue controlQueue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSenderQueues(MessageQueue eventQueue,
			MessageQueue controlQueue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MessageQueue getControlSenderQueue(MessageQueue eventQueue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageQueue getControlReceiverQueue(MessageQueue eventQueue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageQueue getEventSenderQueue(MessageQueue eventQueue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageQueue getEventReceiverQueue(MessageQueue eventQueue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageQueue getStreamingMessageQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStreamingMessageQueue(MessageQueue streamQueue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addToQueue(COMM_QUEUE_TYPE queueType, Ledger message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeFromQueue(COMM_QUEUE_TYPE queueType, Ledger message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Ledger> getQueueData(COMM_QUEUE_TYPE queueType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean sendControlMessage(Ledger message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendEventMessage(Ledger message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendStreamMessage(Ledger message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IPortFactory getPortFactory() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean restartComms() {
		// TODO Auto-generated method stub
		return false;
	}

}
