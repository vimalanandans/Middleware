package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.Event;

public class ResponseKing extends Event {
    public static final String TOPIC = ResponseKing.class.getSimpleName();
    private String location;
    private String king;

    public ResponseKing(String location, String king) {
        super(Stripe.REPLY, TOPIC);
        this.location = location;
        this.king = king;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the king
     */
    public String getKing() {
        return king;
    }

}
