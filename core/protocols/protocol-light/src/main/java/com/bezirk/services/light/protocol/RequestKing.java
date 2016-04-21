package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.Event;

public class RequestKing extends Event {
    public static final String TOPIC = RequestKing.class.getSimpleName();
    private String location;

    public RequestKing(String location) {
        super(Stripe.REQUEST, TOPIC);
        this.location = location;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

}
