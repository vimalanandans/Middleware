package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.Event;

public class TestLocationAndLightSetUpRequest extends Event {

    public static final String TOPIC = TestLocationAndLightSetUpRequest.class.getSimpleName();

    public TestLocationAndLightSetUpRequest() {
        super(Stripe.REQUEST, TOPIC);
        // TODO Auto-generated constructor stub
    }


}
