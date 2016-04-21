/**
 * Smart Service Role
 *
 * @author Cory Henson
 * @modified 09/16/2014
 */
package com.bezirk.protocols.smartservice;

import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.protocols.dragonfly.Observation;
import com.bezirk.protocols.dragonfly.UserObservation;

public class SmartServiceRole extends ProtocolRole {
    private static final String[] events = {
            //Preference.topic,
            Observation.topic,
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