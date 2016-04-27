/**
 * This file is part of Bezirk-Middleware-API.
 * <p>
 * Bezirk-Middleware-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * </p>
 * <p>
 * Bezirk-Middleware-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * </p>
 * You should have received a copy of the GNU General Public License
 * along with Bezirk-Middleware-API.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bezirk.middleware.messages;

/**
 * Base class for simple Bezirk messages. An event represents a simple message such as a personalization
 * observation, temperature measurement, etc. This class is extended by protocol implementations to
 * define concrete events and their custom attributes and simple, small payloads.
 * <p>
 * An <code>Event</code> is used to represent simple messages that communicate one request, reply, or
 * notification. To combine multiple messages into one send or to include non-trivial message
 * payloads, use the {@link Stream} class.
 * </p>
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
