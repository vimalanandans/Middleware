/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.middleware.messages.Event;

/**
 * @see com.bezirk.API.messages.Event
 */
public class RequestUIpickOne extends Event {

    /**
     * Useful for static references to the event topic, e.g. protocol definitions
     */
    public static final String TOPIC = RequestUIpickOne.class.getSimpleName();

    // Payload
    private String intro;
    private String[] availableChoices;
    private long expiration;

    /**
     * Request user to choose one among options
     *
     * @param intro            explain to the user WHY the choice is needed
     * @param availableChoices
     * @param expiration       : how long to offer the request
     */
    public RequestUIpickOne(String intro, String[] availableChoices, long expiration) {
        // set UhuMessage properties: useful for discrimination/deserialization
        super(Flag.REQUEST, TOPIC);
        // set event payload: useful for the consumer services
        this.intro = intro;
        this.availableChoices = availableChoices == null ? null : availableChoices.clone();
        this.expiration = expiration;
    }

    /**
     * Use instead of the generic UhuMessage.fromJson()
     *
     * @param json
     * @return
     */
    public static RequestUIpickOne deserialize(String json) {
        return Event.fromJson(json, RequestUIpickOne.class);
    }

    public String getIntro() {
        return intro;
    }

    public String[] getAvailableChoices() {
        return availableChoices == null ? null : availableChoices.clone();
    }

    public long getExpiration() {
        return expiration;
    }
}
