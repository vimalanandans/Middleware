package com.bezirk.middleware.core;

import com.bezirk.middleware.messages.Event;

public class AirQualityUpdateEvent extends Event {
    public String sender;
    public double
            humidity /* decimal, e.g., 0.5 */,
            dustLevel /* milligrams per cubic meter. Above 20 is high. */,
            pollenLevel /* grams per cubic meter. Above 500 is high. */;

    @Override
    public String toString() {
        return "AirQualityUpdateEvent{" +
                "sender='" + sender + '\'' +
                ", pollenLevel=" + pollenLevel +
                "} ";
    }
}