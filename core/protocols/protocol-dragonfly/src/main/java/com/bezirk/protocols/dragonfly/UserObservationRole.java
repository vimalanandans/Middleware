/**
 * UserObservationRole
 *
 * @author Cory Henson
 * @modified 10/27/2014
 */
package com.bezirk.protocols.dragonfly;

import com.bezirk.middleware.messages.ProtocolRole;

public class UserObservationRole extends ProtocolRole {
    private static final String[] events = {
            UserObservation.topic
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
