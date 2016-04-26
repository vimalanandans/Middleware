package com.bezirk.test.ctrlmessages;

import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.control.messages.pipes.PipeMulticastHeader;
import com.bezirk.control.messages.pipes.PipeUnicastHeader;
import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

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
        UhuZirkId id = new UhuZirkId("test-id-string");
        UhuZirkEndPoint senderSEP = new UhuZirkEndPoint(id);
        pipeMulticastHeader.setSenderSEP(senderSEP);
        pipeMulticastHeader.setTopic("test-topic");

        return pipeMulticastHeader;
    }

    private PipeUnicastHeader createPipeUnicastHeader() {
        PipeUnicastHeader pipeUnicastHeader = new PipeUnicastHeader();
        UhuZirkId recipientId = new UhuZirkId("test-id-recipient");
        UhuZirkEndPoint recipientSEP = new UhuZirkEndPoint(recipientId);
        pipeUnicastHeader.setRecipient(recipientSEP);
        UhuZirkId senderId = new UhuZirkId("test-id-sender");
        UhuZirkEndPoint senderSEP = new UhuZirkEndPoint(senderId);
        pipeUnicastHeader.setSenderSEP(senderSEP);
        pipeUnicastHeader.setTopic("test-topic");

        return pipeUnicastHeader;
    }

}
