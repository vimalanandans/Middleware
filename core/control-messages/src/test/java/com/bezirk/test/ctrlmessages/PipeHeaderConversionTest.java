package com.bezirk.test.ctrlmessages;

import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.control.messages.pipes.PipeMulticastHeader;
import com.bezirk.control.messages.pipes.PipeUnicastHeader;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PipeHeaderConversionTest {


    @Test
    public void convertMulticastHeader() throws Exception {
        PipeMulticastHeader pipeMulticastHeader = createPipeMulticastHeader();
        MulticastHeader multicastHeader = pipeMulticastHeader.toMulticastHeader();
        assertNotNull(multicastHeader);
        assertNotNull(multicastHeader.getRecipientSelector());
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
        RecipientSelector recipientSelector = new RecipientSelector(location);
        pipeMulticastHeader.setRecipientSelector(recipientSelector);
        ZirkId id = new ZirkId("test-id-string");
        BezirkZirkEndPoint senderSEP = new BezirkZirkEndPoint(id);
        pipeMulticastHeader.setSenderSEP(senderSEP);
        pipeMulticastHeader.setTopic("test-topic");

        return pipeMulticastHeader;
    }

    private PipeUnicastHeader createPipeUnicastHeader() {
        PipeUnicastHeader pipeUnicastHeader = new PipeUnicastHeader();
        ZirkId recipientId = new ZirkId("test-id-recipient");
        BezirkZirkEndPoint recipientSEP = new BezirkZirkEndPoint(recipientId);
        pipeUnicastHeader.setRecipient(recipientSEP);
        ZirkId senderId = new ZirkId("test-id-sender");
        BezirkZirkEndPoint senderSEP = new BezirkZirkEndPoint(senderId);
        pipeUnicastHeader.setSenderSEP(senderSEP);
        pipeUnicastHeader.setTopic("test-topic");

        return pipeUnicastHeader;
    }

}
