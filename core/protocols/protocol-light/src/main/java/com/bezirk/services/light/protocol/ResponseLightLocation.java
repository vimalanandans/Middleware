package com.bezirk.services.light.protocol;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.Event;

public class ResponseLightLocation extends Event {
    public static final String TOPIC = ResponseLightLocation.class.getSimpleName();
    private Integer lightId;
    private Location location;

    public ResponseLightLocation(Integer lightId, Location location) {
        super(Flag.REPLY, TOPIC);
        this.lightId = lightId;
        this.location = location;
    }

    /**
     * @return the lightId
     */
    public Integer getLightId() {
        return lightId;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }


}
