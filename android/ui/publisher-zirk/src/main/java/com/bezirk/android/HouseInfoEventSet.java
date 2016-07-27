package com.bezirk.android;

import com.bezirk.middleware.messages.EventSet;

public class HouseInfoEventSet extends EventSet {
    public HouseInfoEventSet() {
        super(AirQualityUpdateEvent.class, UpdateAcceptedEvent.class);
    }
}