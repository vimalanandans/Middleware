package com.bezirk.util;

import com.bezirk.middleware.messages.ProtocolRole;

/**
 * This is mock protocol role used in unit testing.
 */
public class MockProtocolRole extends ProtocolRole {
    private static final String description = "Protocol Used for testing.";
    private final String[] eventTopics = new String[]{"MockEvent1", "MockEvent2"};
    private final String[] streamTopics = new String[]{"MockStream1", "MockStream2"};
    private String protocolName = getClass().getSimpleName();

    /* (non-Javadoc)
     * @see ProtocolRole#getProtocolName()
     */
    @Override
    public String getProtocolName() {
        // TODO Auto-generated method stub
        return protocolName;
    }

    /* (non-Javadoc)
     * @see ProtocolRole#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see ProtocolRole#getEventTopics()
     */
    @Override
    public String[] getEventTopics() {
        return eventTopics;
    }

    /* (non-Javadoc)
     * @see ProtocolRole#getStreamTopics()
     */
    @Override
    public String[] getStreamTopics() {
        return streamTopics;
    }

}
