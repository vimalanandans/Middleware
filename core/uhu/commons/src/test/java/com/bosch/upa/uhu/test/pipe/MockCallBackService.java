package com.bosch.upa.uhu.test.pipe;

import com.bosch.upa.uhu.messagehandler.DiscoveryIncomingMessage;
import com.bosch.upa.uhu.messagehandler.EventIncomingMessage;
import com.bosch.upa.uhu.messagehandler.ServiceMessageHandler;
import com.bosch.upa.uhu.messagehandler.MulticastCallbackMessage;
import com.bosch.upa.uhu.messagehandler.PipeRequestIncomingMessage;
import com.bosch.upa.uhu.messagehandler.StreamStatusMessage;
import com.bosch.upa.uhu.messagehandler.StreamIncomingMessage;

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
