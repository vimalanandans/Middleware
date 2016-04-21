/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.middleware.messages.Event;

/**
 * @see com.bezirk.API.messages.Event
 */
public class RequestUIinputValues extends Event {

    /**
     * Useful for static references to the event topic, e.g. protocol definitions
     */
    public static final String TOPIC = RequestUIinputValues.class.getSimpleName();

    // Payload
    private InputValuesStringTriplet[] values;
    private long expiration;

    /**
     * Request user input on values
     *
     * @param values
     * @param expiration : how long to offer the request
     */
    public RequestUIinputValues(InputValuesStringTriplet[] values, long expiration) {
        // set UhuMessage properties: useful for discrimination/deserialization
        super(Flag.REQUEST, TOPIC);
        // set event payload: useful for the consumer services
        this.values = values == null ? null : values.clone();
        this.expiration = expiration;
    }

    /**
     * Use instead of the generic UhuMessage.deserialize()
     *
     * @param json
     * @return
     */
    public static RequestUIinputValues deserialize(String json) {
        return Event.deserialize(json, RequestUIinputValues.class);
    }

    public InputValuesStringTriplet[] getValues() {
        return values == null ? null : values.clone();
    }

    public long getExpiration() {
        return expiration;
    }
}
