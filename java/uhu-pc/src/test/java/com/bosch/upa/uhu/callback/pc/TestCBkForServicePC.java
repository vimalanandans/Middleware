package com.bosch.upa.uhu.callback.pc;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.messagehandler.DiscoveryIncomingMessage;
import com.bosch.upa.uhu.messagehandler.EventIncomingMessage;
import com.bosch.upa.uhu.messagehandler.PipeRequestIncomingMessage;
import com.bosch.upa.uhu.messagehandler.ServiceIncomingMessage;
import com.bosch.upa.uhu.messagehandler.StreamIncomingMessage;
import com.bosch.upa.uhu.messagehandler.StreamStatusMessage;

/**
 * This testcase verifies the working of Callback service for PC.
 * 
 * @modified by AJC6KOR
 *
 */
public class TestCBkForServicePC {

	private final IBoradcastReceiver BRForService = new BRForServiceMock();
	private final CBkForServicePC cBkForServicePC= new CBkForServicePC(BRForService);
	private boolean receivedEvent = false;
	private boolean receivedUnicastStream = false;
	private boolean receivedStreamStatus = false;	
	private boolean receivedDiscovery = false;	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestCBkForServicePC.class);
	
	@BeforeClass
	public static void setUpClass(){
		LOGGER.info("********** Setting up TestCBkForServicePC Testcase **********");
		
	}
	
	
	@AfterClass
	public static void tearDownClass(){
		LOGGER.info("********** Shutting down TestCBkForServicePC Testcase **********");
	}
	
	private enum CallBackDiscriminator {
		
		EVENT,STREAM_UNICAST,STREAM_STATUS,DISCOVERY;
		
	}
	
	@Test
	public void test() {

		testFireEventCallback();
		
		testFireUnicastStreamCallback();
		
		testFireStreamStatusCallback();	
		
		testFireDiscoveryCallback();
		
	}
	
	private void testFireEventCallback(){
		EventIncomingMessage eventCallbackMessage = new EventIncomingMessage();
		cBkForServicePC.onIncomingEvent(eventCallbackMessage);
		
		assertTrue("Callback Service is unable to fire eventCallback. ",receivedEvent);
	}
	
	private void testFireUnicastStreamCallback(){
		StreamIncomingMessage unicastStreamCallbackMessage= new StreamIncomingMessage();
		cBkForServicePC.onIncomingStream(unicastStreamCallbackMessage);
		
		assertTrue("Callback Service is unable to fire Unicast stream.",receivedUnicastStream);

	}
	
	@Test(expected=RuntimeException.class)
	public void testFirePipeApprovedCallBack(){
		PipeRequestIncomingMessage pipeMsg= new PipeRequestIncomingMessage();
		cBkForServicePC.onPipeApprovedMessage(pipeMsg);
	}
	
	private void testFireStreamStatusCallback(){
		StreamStatusMessage streamStatusCallbackMessage = new StreamStatusMessage();
		cBkForServicePC.onStreamStatus(streamStatusCallbackMessage);
		
		assertTrue("Callback Service is unable to fire stream status.",receivedStreamStatus);

	}
	
	private void testFireDiscoveryCallback(){
		DiscoveryIncomingMessage discoveryCallbackMessage = new DiscoveryIncomingMessage();
		cBkForServicePC.onDiscoveryIncomingMessage(discoveryCallbackMessage);
		
		assertTrue("Callback Service is unable to fire Discovery callback.",receivedDiscovery);

	}

	class  BRForServiceMock implements IBoradcastReceiver{
		
		@Override
		public void onReceive(ServiceIncomingMessage callbackMessage) {
			
			if(callbackMessage.getCallbackType().equalsIgnoreCase(CallBackDiscriminator.EVENT.name())){
				
				receivedEvent= true;
				
			}else if(callbackMessage.getCallbackType().equalsIgnoreCase(CallBackDiscriminator.STREAM_UNICAST.name())){
				
				receivedUnicastStream = true;
				
			}else if(callbackMessage.getCallbackType().equalsIgnoreCase(CallBackDiscriminator.STREAM_STATUS.name())){
				
				receivedStreamStatus = true;
			}else if(callbackMessage.getCallbackType().equalsIgnoreCase(CallBackDiscriminator.DISCOVERY.name())){
				
				receivedDiscovery = true;
			}
		}

		
	}
}