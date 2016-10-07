package com.bezirk.middleware.addressing;

import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * Aggregates addressing information for publishing {@link Event events} using the Bezirk middleware.
 * Typically you should think about your Zirk broadcasting messages to other Zirks within your Zirk's
 * subnet where the message will be filtered based on topic. This class allows you to filter
 * by more than simply topic by narrowing the scope further using a semantic address specified
 * by a {@link Location}.
 * <p>
 * This class is used when sending messages with
 * {@link com.bezirk.middleware.Bezirk#sendEvent(RecipientSelector, Event)}.
 * </p>
 *
 * @see Event
 * @see StreamDescriptor
 * @see Location
 */
public class RecipientSelector implements Serializable {

    private static final Gson gson;
    private static final long serialVersionUID = -6831867323290640758L;

    static {
        final GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }

    private final Location location;

    /**
     * RecipientSelector for specifying a message's recipient set within a Zirk's subnet.
     *
     * @param location the semantic address used to narrow a message's set of recipients farther
     *                 than a topic does
     */
    public RecipientSelector(Location location) {
        this.location = location;
    }

    /**
     * Deserialize a JSON string representing an address to create an <code>RecipientSelector</code> object.
     *
     * @param serializedAddress the JSON String that is to be deserialized
     * @return an <code>RecipientSelector</code> object deserialized from <code>json</code>
     */
    public static RecipientSelector fromJson(String serializedAddress) {
        return gson.fromJson(serializedAddress, RecipientSelector.class);
    }

    /**
     * Returns this address's semantic address, or <code>null</code> if no location
     * is set
     *
     * @return this address's semantic address, or <code>null</code> if no location
     * is set
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Serialize the address to a JSON string.
     *
     * @return JSON representation of the address
     */
    public String toJson() {
        return gson.toJson(this);
    }
}