package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.Event;

public class DiscoverLocations extends Event {
    public final static String TOPIC = DiscoverLocations.class.getSimpleName();

    public DiscoverLocations() {
        super(Flag.NOTICE, TOPIC);
    }

}
