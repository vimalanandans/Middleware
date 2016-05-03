/**
 * @author Cory Henson
 */
package com.bezirk.protocols.presence;

import com.bezirk.middleware.messages.ProtocolRole;

public class PresenceDetectorRole extends ProtocolRole {
    private static final String[] events = {};
    private String role = this.getClass().getSimpleName();

    @Override
    public String getRoleName() {
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