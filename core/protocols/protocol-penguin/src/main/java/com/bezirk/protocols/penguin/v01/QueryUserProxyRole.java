/**
 * @author Cory Henson
 */
package com.bezirk.protocols.penguin.v01;

import com.bezirk.middleware.messages.ProtocolRole;

public class QueryUserProxyRole extends ProtocolRole {
    private static final String[] events = {
            //Preference.topic,
            UserProfile.topic
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
