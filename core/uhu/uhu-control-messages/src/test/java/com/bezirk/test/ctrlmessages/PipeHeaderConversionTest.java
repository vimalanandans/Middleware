package com.bezirk.test.ctrlmessages;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bezirk.api.addressing.Address;
import com.bezirk.api.addressing.Location;
import com.bosch.upa.uhu.control.messages.MulticastHeader;
import com.bosch.upa.uhu.control.messages.UnicastHeader;
import com.bosch.upa.uhu.control.messages.pipes.PipeMulticastHeader;
import com.bosch.upa.uhu.control.messages.pipes.PipeUnicastHeader;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

public class PipeHeaderConversionTest {
	
	
	@Test
	public void convertMulticastHeader() throws Exception {
		PipeMulticastHeader pipeMulticastHeader = createPipeMulticastHeader();
		MulticastHeader multicastHeader = pipeMulticastHeader.toMulticastHeader();
		assertNotNull(multicastHeader);
		assertNotNull(multicastHeader.getAddress());
		assertNotNull(multicastHeader.getSenderSEP());
		assertNotNull(multicastHeader.getTopic());
	}
	
	@Test
	public void convertUnicastHeader() throws Exception {
		PipeUnicastHeader pipeUnicastHeader = createPipeUnicastHeader();
		UnicastHeader unicastHeader = pipeUnicastHeader.toUnicastHeader();
		assertNotNull(unicastHeader);
		assertNotNull(unicastHeader.getRecipient());
		assertNotNull(unicastHeader.getSenderSEP());
		assertNotNull(unicastHeader.getTopic());
	}
	
	private PipeMulticastHeader createPipeMulticastHeader() {
		PipeMulticastHeader pipeMulticastHeader = new PipeMulticastHeader();
		Location location = new Location("BobsHouse");
		Address address = new Address(location);
		pipeMulticastHeader.setAddress(address);
		UhuServiceId id = new UhuServiceId("test-id-string");
		UhuServiceEndPoint senderSEP = new UhuServiceEndPoint(id);
		pipeMulticastHeader.setSenderSEP(senderSEP);
		pipeMulticastHeader.setTopic("test-topic");
		
		return pipeMulticastHeader;
	}
	
	private PipeUnicastHeader createPipeUnicastHeader() {
		PipeUnicastHeader pipeUnicastHeader = new PipeUnicastHeader();
		UhuServiceId recipientId = new UhuServiceId("test-id-recipient");
		UhuServiceEndPoint recipientSEP = new UhuServiceEndPoint(recipientId);
		pipeUnicastHeader.setRecipient(recipientSEP);
		UhuServiceId senderId = new UhuServiceId("test-id-sender");
		UhuServiceEndPoint senderSEP = new UhuServiceEndPoint(senderId);
		pipeUnicastHeader.setSenderSEP(senderSEP);
		pipeUnicastHeader.setTopic("test-topic");
		
		return pipeUnicastHeader;
	}

}
