package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.ProtocolRole;

public class LightConfigConsumerRole extends ProtocolRole {

    static final String description = "This the protocol role that the light ui must subscribe to";
    private static final String evts[] =
            {ResponseActiveLights.TOPIC,
                    ResponseLightLocation.TOPIC,
                    ResponseKing.TOPIC,
                    MakeKing.TOPIC,
                    ResponsePolicy.TOPIC};

    @Override
    public String getProtocolName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String[] getEventTopics() {
        return evts == null ? null : evts.clone();
    }

    @Override
    public String[] getStreamTopics() {
        // TODO Auto-generated method stub
        return null;
    }

}
