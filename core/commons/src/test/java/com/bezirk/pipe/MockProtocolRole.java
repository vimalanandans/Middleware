package com.bezirk.pipe;

import com.bezirk.middleware.messages.ProtocolRole;

public class MockProtocolRole extends ProtocolRole {

    private String name = getClass().getSimpleName();

    @Override
    public String getRoleName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Protocol used for testing";
    }

    @Override
    public String[] getEventTopics() {
        return new String[]{"topic1", "topic2"};
    }

    @Override
    public String[] getStreamTopics() {
        return null;
    }

}
