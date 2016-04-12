package com.bezirk.util;

import java.net.InetAddress;
import java.util.ArrayList;

import com.bezirk.comms.CommsProperties;

import com.bezirk.comms.ICommsNotification;
import com.bezirk.comms.ICtrlMsgReceiver;
import com.bezirk.comms.IUhuComms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.pipe.core.PipeManager;
import com.bezirk.sadl.UhuSadlManager;
import com.bezirk.sphere.api.IUhuSphereForSadl;
import com.bezirk.streaming.control.Objects.StreamRecord;

public class MockComms implements IUhuComms {

	private ArrayList<ControlLedger> ctrlList;
	
	private ArrayList<EventLedger> eventList;
	
	private ArrayList<StreamRequest> streamList;

	public ArrayList<ControlLedger> getCtrlList() {
		return ctrlList;
	}

	public void setCtrlList(ArrayList<ControlLedger> ctrlList) {
		this.ctrlList = ctrlList;
	}

	public ArrayList<EventLedger> getEventList() {
		return eventList;
	}

	public void setEventList(ArrayList<EventLedger> eventList) {
		this.eventList = eventList;
	}

	public ArrayList<StreamRequest> getStreamList() {
		return streamList;
	}

	public void setStreamList(ArrayList<StreamRequest> streamList) {
		this.streamList = streamList;
	}

	@Override
	public boolean startComms() {
		ctrlList = new ArrayList<>();
		eventList = new ArrayList<>();
		streamList = new ArrayList<>();
		return true;
	}

	@Override
	public boolean stopComms() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean closeComms() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean sendMessage(Ledger message) {

		if (message instanceof ControlLedger) {

			ControlLedger ctrlLedger = (ControlLedger) message;
			ControlMessage ledgerMessage = ctrlLedger.getMessage();

			if (ledgerMessage instanceof StreamRequest) {
				StreamRequest streamMessage = (StreamRequest) ledgerMessage;
				streamList.add(streamMessage);
				return true;
			} else {

				ctrlList.add(ctrlLedger);
				return true;
			}
		} else if (message instanceof EventLedger) {
			eventList.add((EventLedger) message);
			return true;
		}
		return false;
	}

	@Override
	public boolean sendStream(String uniqueKey) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean registerStreamBook(String key, StreamRecord sRecord) {
		// TODO Auto-generated method stub
		return true;
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
		return true;
	}

	@Override
	public boolean registerControlMessageReceiver(Discriminator id,
			ICtrlMsgReceiver receiver) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setSphereForSadl(IUhuSphereForSadl uhuSphere) {
		// TODO Auto-generated method stub

	}

	public void clearQueues() {

		ctrlList.clear();
		eventList.clear();
		streamList.clear();

	}
	
	@Override
	public boolean restartComms() {
		// TODO Auto-generated method stub
		return false;
	}

}
