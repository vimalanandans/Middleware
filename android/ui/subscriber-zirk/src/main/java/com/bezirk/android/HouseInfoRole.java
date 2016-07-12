package com.bezirk.android;

import com.bezirk.middleware.messages.ProtocolRole;

public class HouseInfoRole extends ProtocolRole {
    public String getRoleName() {
        return "House Information Role";
    }

    public String getDescription() {
        return "This role contains topics regarding house information.";
    }

    public String[] getEventTopics() {
        String[] topics = {AirQualityUpdateEvent.TOPIC, UpdateAcceptedEvent.TOPIC};
        return topics;
    }

    public String[] getStreamTopics() {
        return null;
    }
}