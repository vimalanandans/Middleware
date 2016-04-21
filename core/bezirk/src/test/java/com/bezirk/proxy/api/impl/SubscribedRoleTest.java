package com.bezirk.proxy.api.impl;

import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.util.MockProtocolRole;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * This testcase verifies the SubscribedRole by checking the event and stream topics.
 *
 * @author AJC6KOR
 */
public class SubscribedRoleTest {

    @Test
    public void test() {


        ProtocolRole pRole = new MockProtocolRole();
        com.bezirk.proxy.api.impl.SubscribedRole subscribedRole = new com.bezirk.proxy.api.impl.SubscribedRole(pRole);

        assertEquals("Description is not same as the mockprotocol description.", pRole.getDescription(), subscribedRole.getDescription());
        assertEquals("SubscribedRole name is not same as the mockprotocol name.", pRole.getProtocolName(), subscribedRole.getProtocolName());
        assertTrue("EventTopics is not same as the mockprotocol events.", Arrays.equals(pRole.getEventTopics(), subscribedRole.getEventTopics()));
        assertTrue("StreamTopics is not same as the mockprotocol streams.", Arrays.equals(pRole.getStreamTopics(), subscribedRole.getStreamTopics()));

        String protocolAsString = subscribedRole.getSubscribedProtocolRole();
        assertEquals("Protocol is not matching with the set value.", subscribedRole.toString(), subscribedRole.getProtocolRole(protocolAsString).toString());

    }

}
