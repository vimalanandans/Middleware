package com.bezirk.middleware.addressing;

import com.bezirk.middleware.messages.ProtocolRole;

class MockProtocolRole extends ProtocolRole {

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
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ProtocolRole#getEventTopics()
     */
    @Override
    public String[] getEventTopics() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ProtocolRole#getStreamTopics()
     */
    @Override
    public String[] getStreamTopics() {
        // TODO Auto-generated method stub
        return null;
    }

}
