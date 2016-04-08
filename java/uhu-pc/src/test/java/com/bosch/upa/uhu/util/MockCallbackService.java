package com.bosch.upa.uhu.util;

import com.bosch.upa.uhu.messagehandler.DiscoveryIncomingMessage;
import com.bosch.upa.uhu.messagehandler.EventIncomingMessage;
import com.bosch.upa.uhu.messagehandler.PipeRequestIncomingMessage;
import com.bosch.upa.uhu.messagehandler.ServiceMessageHandler;
import com.bosch.upa.uhu.messagehandler.StreamIncomingMessage;
import com.bosch.upa.uhu.messagehandler.StreamStatusMessage;

/**
 * Mock callback service implementing IUhuCallback, used for unit testing
 * 
 * @author AJC6KOR
 *
 */
public class MockCallbackService implements ServiceMessageHandler {

	@Override
	public void onIncomingEvent(EventIncomingMessage eventIncomingMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onIncomingStream(StreamIncomingMessage streamIncomingMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStreamStatus(StreamStatusMessage streamStatusMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDiscoveryIncomingMessage(
			DiscoveryIncomingMessage discoveryCallback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPipeApprovedMessage(PipeRequestIncomingMessage pipeMsg) {
		// TODO Auto-generated method stub
		
	}

}
