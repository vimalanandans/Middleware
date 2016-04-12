package com.bezirk.test.pipe;

import com.bezirk.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.messagehandler.EventIncomingMessage;
import com.bezirk.messagehandler.ServiceMessageHandler;
import com.bezirk.messagehandler.PipeRequestIncomingMessage;
import com.bezirk.messagehandler.StreamStatusMessage;
import com.bezirk.messagehandler.StreamIncomingMessage;

public class MockCallBackService implements ServiceMessageHandler {
	
	private MockUhuService mockUhuservice = null;

	public MockCallBackService(MockUhuService mockUhuservice) {
		super();
		this.mockUhuservice = mockUhuservice;
	}

	@Override
	public void onIncomingEvent(EventIncomingMessage eventIncomingMessage) {
		
	}

	@Override
	public void onIncomingStream(
			StreamIncomingMessage streamIncomingMessage) {
		
	}


	@Override
	public void onStreamStatus(
			StreamStatusMessage streamStatusMessage) {
		
	}

	@Override
	public void onDiscoveryIncomingMessage(DiscoveryIncomingMessage discoveryCallback) {
		
	}

	@Override
	public void onPipeApprovedMessage(PipeRequestIncomingMessage pipeMsg) {
		
			this.mockUhuservice.setPipeGranted(true);
			this.mockUhuservice.setPipeGrantedCalled(true);
		
		
		
	}

}
