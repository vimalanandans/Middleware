package com.bezirk.android;

import com.bezirk.middleware.messages.Event;

public class AirQualityUpdateEvent extends Event {
    public static final String TOPIC = AirQualityUpdateEvent.class.getCanonicalName();

    public double
        humidity /* decimal, e.g., 0.5 */,
        dustLevel /* milligrams per cubic meter. Above 20 is high. */,
        pollenLevel /* grams per cubic meter. Above 500 is high. */;

    public AirQualityUpdateEvent() {
        super(Flag.NOTICE, TOPIC);
    }

    public String toString() {
        return "humidity: " + humidity + " dustLevel: "
                + dustLevel + " pollenLevel: " + pollenLevel;
    }
}