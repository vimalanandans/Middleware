package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.Event;

import java.util.List;

public class DiscoverLocationResponse extends Event {

    public final static String TOPIC = DiscoverLocationResponse.class.getSimpleName();
    private List<String> knownLocations;

    public DiscoverLocationResponse() {
        super(Flag.REPLY, TOPIC);
    }

    public List<String> getKnownLocations() {
        return knownLocations;
    }

    public void setKnownLocations(List<String> knownLocations) {
        this.knownLocations = knownLocations;
    }


}
