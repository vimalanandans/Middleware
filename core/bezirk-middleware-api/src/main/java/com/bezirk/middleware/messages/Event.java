package com.bezirk.middleware.messages;

/**
 * Base class for simple Bezirk messages. An event represents a simple message such as a light color
 * change request, a temperature measurement, etc. This class is extended to define concrete events and
 * their custom attributes and simple, small payloads.
 * <p>
 * An <code>Event</code> is used to represent simple messages that communicate one request, reply, or
 * notification.
 * </p>
 *
 * @see Message
 */
public class Event extends Message {
    private static final long serialVersionUID = 2069566077448432587L;
}
