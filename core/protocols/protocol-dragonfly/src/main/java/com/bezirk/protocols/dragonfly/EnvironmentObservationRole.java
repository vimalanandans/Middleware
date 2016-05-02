/**
 * @author Cory Henson
 */
package com.bezirk.protocols.dragonfly;

import com.bezirk.middleware.messages.ProtocolRole;

public class EnvironmentObservationRole extends ProtocolRole {
    private static final String[] events = {
            EnvironmentObservation.topic
    };
    private String role = this.getClass().getSimpleName();

    @Override
    public String getProtocolName() {
        return role;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String[] getEventTopics() {
        return events == null ? null : events.clone();
    }

    @Override
    public String[] getStreamTopics() {
        return null;
    }
}
