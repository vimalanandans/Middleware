package com.bosch.upa.UhUbasics.protocols.oneway;

import com.bosch.upa.uhu.Proxy.ProtocolRole;

public class HelloNoticeProtocolRole implements ProtocolRole {

    private static final String[] EVENTS = {
            HelloNoticeEvent.TOPIC
    };
    private String ROLE = this.getClass().getSimpleName();

    /**
     * Unique identifier for our protocol role
     */
    @Override
    public String getProtocol() {
        return ROLE;
    }

    /**
     * The set of Events specified by this protocol role
     */
    @Override
    public String[] getEvents() {
        return EVENTS;
    }

    @Override
    public String[] getStreams() {
        return null;
    }

}
