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
package com.bezirk.middleware.addressing;

import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.middleware.serialization.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Aggregates addressing information for publishing {@link Event events} and
 * {@link com.bezirk.middleware.messages.Stream streams} using the Bezirk middleware. Typically
 * you should think about your Zirk broadcasting messages to other Zirks within your Zirk's
 * sphere(s) where the message will be filtered based on topic. This class allows you to filter
 * by more than simply topic by narrowing the scope further using a semantic address specified
 * by a {@link Location}. You can also use this class to specify a pipe to broaden the scope to
 * Zirks or endpoints outside of your Zirk's sphere(s).
 * <p>
 * This class is used when sending
 * {@link com.bezirk.middleware.Bezirk#sendEvent(ServiceId, Address, Event) events} or
 * {@link com.bezirk.middleware.Bezirk#sendStream(ServiceId, ServiceEndPoint, Stream, String) streams}, when
 * {@link com.bezirk.middleware.Bezirk#discover(ServiceId, Address, ProtocolRole, long, int, BezirkListener) discovering}
 * services, and in any other context where it is useful to narrow a message's set of recipients
 * beyond what can be achieved with simply a {@link ProtocolRole}.
 * </p>
 *
 * @see Event
 * @see com.bezirk.middleware.messages.Stream
 * @see Location
 * @see ProtocolRole
 */
public class Address {
    private final Location location;
    private final Pipe pipe;
    private static final Gson gson;

    static {
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Pipe.class, new InterfaceAdapter<Pipe>());
        gson = builder.create();
    }

    /**
     * Address for narrowing a message's recipient set within a Zirk's sphere(s). Use
     * {@link #Address(Location, Pipe)} if you'd like to also specify a pipe.
     *
     * @param location the semantic address used to narrow a message's set of recipients farther
     *                 than a topic does
     */
    public Address(Location location) {
        this.location = location;
        pipe = null;
    }

    /**
     * Address for broadening a message's set of recipients to Zirks or endpoints outside of
     * a Zirk's sphere(s) using a pipe.  Use
     * {@link #Address(Location, Pipe)} if you'd like to also specify a semantic address.
     *
     * @param pipe the specific pipe the message should also be sent on, or <code>null</code>
     *             for all authorized pipes in the Zirk's sphere(s)
     */
    public Address(Pipe pipe) {
        location = null;
        this.pipe = pipe;
    }

    /**
     * Address for narrowing a message's set of recipients to some degree using semantic address and
     * broadening the set of recipients to Zirks or endpoints outside of a Zirk's sphere(s) using a
     * pipe.
     *
     * @param location the semantic address used to narrow a message's set of recipients farther than
     *                 a topic does
     * @param pipe     the specific pipe the message should also be sent on, or <code>null</code>
     *                 for all authorized pipes in the Zirk's sphere(s)
     */
    public Address(Location location, Pipe pipe) {
        this.location = location;
        this.pipe = pipe;
    }

    /**
     * Returns this address's pipe, or <code>null</code> if no pipe is set
     *
     * @return this address's pipe or <code>null</code> if no pipe is set
     */
    public Pipe getPipe() {
        return pipe;
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

    /**
     * Deserialize a JSON string representing an address to create an <code>Address</code> object.
     *
     * @param serializedAddress the JSON String that is to be deserialized
     * @return an <code>Address</code> object deserialized from <code>json</code>
     */
    public static Address fromJson(String serializedAddress) {
        return gson.fromJson(serializedAddress, Address.class);
    }
}
