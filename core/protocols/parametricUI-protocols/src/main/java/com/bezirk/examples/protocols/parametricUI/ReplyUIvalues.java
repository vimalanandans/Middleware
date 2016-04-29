/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.middleware.messages.Event;

/**
 * @see com.bezirk.API.messages.Event
 */
public class ReplyUIvalues extends Event {

    /**
     * Useful for static references to the event topic, e.g. protocol definitions
     */
    public static final String TOPIC = ReplyUIvalues.class.getSimpleName();

    // Payload
    private InputValuesStringPair[] values;

    /**
     * A reply with the user's chosen values
     *
     * @param values
     */
    public ReplyUIvalues(InputValuesStringPair[] values) {
        // set Message properties: useful for discrimination/deserialization
        super(Flag.REPLY, TOPIC);
        // set event payload: useful for the consumer services
        this.values = values == null ? null : values.clone();
    }

    /**
     * Use instead of the generic Message.fromJson()
     *
     * @param json
     * @return
     */
    public static ReplyUIvalues deserialize(String json) {
        return Event.fromJson(json, ReplyUIvalues.class);
    }

    public InputValuesStringPair[] getValues() {
        return values == null ? null : values.clone();
    }
}
