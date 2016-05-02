/**
 * @author Cory Henson
 */
package com.bezirk.protocols.penguin.v01;

import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.protocols.dragonfly.Observation;
import com.bezirk.protocols.dragonfly.UserObservation;
import com.bezirk.protocols.dragonfly.test.ObservationTest;
import com.bezirk.protocols.penguin.v01.test.GetPreferenceTest;
import com.bezirk.protocols.penguin.v01.test.GetUserProfileTest;
import com.bezirk.protocols.specific.userproxy.ClearData;

public class UserProxyRole extends ProtocolRole {
    private static final String[] events = {
            GetPreference.topic,
            GetPreferenceTest.topic,
            Observation.topic,
            ObservationTest.topic,
            GetUserProfile.topic,
            GetUserProfileTest.topic,
            InitUserProxyUI.topic,
            InitUserProxyRemoteUI.topic,
            UserObservation.topic,
            ClearData.topic
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