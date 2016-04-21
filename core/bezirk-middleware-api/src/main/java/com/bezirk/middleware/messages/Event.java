/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 * <p/>
 * Authors: Joao de Sousa, 2014
 * Mansimar Aneja, 2014
 * Vijet Badigannavar, 2014
 * Samarjit Das, 2014
 * Cory Henson, 2014
 * Sunil Kumar Meena, 2014
 * Adam Wynne, 2014
 * Jan Zibuschka, 2014
 */
package com.bezirk.middleware.messages;

/**
 * Base class for simple Bezirk messages. An event represents a simple message such as a personalization
 * observation, temperature measurement, etc. This class is extended by protocol implementations to
 * define concrete events and their custom attributes and simple, small payloads.
 * <p/>
 * An <code>Event</code> is used to represent simple messages that communicate one request, reply, or
 * notification. To combine multiple messages into one send or to include non-trivial message
 * payloads, use the {@link Stream} class.
 *
 * @see Message
 * @see Stream
 */
public class Event extends Message {
    /**
     * The concrete implentation of an <code>Event</code> must specify the event's flag
     * and topic. Message flags and topics are documented in {@link Message}.
     *
     * @param flag  flag to mark the intent of this event
     * @param topic the pub-sub topic for this event
     */
    public Event(Flag flag, String topic) {
        this.flag = flag;
        this.topic = topic;
    }
}
