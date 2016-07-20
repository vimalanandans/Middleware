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
package com.bezirk.middleware.addressing;

import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Aggregates addressing information for publishing {@link Event events} and
 * {@link StreamDescriptor streams} using the Bezirk middleware. Typically
 * you should think about your Zirk broadcasting messages to other Zirks within your Zirk's
 * sphere(s) where the message will be filtered based on topic. This class allows you to filter
 * by more than simply topic by narrowing the scope further using a semantic address specified
 * by a {@link Location}. You can also use this class to specify a pipe to broaden the scope to
 * Zirks or endpoints outside of your Zirk's sphere(s).
 * <p>
 * This class is used when sending
 * {@link com.bezirk.middleware.Bezirk#sendEvent(RecipientSelector, Event) events} or
 * {@link com.bezirk.middleware.Bezirk#sendStream(ZirkEndPoint, StreamDescriptor, java.io.File) streams} and
 * in any other context where it is useful to narrow a message's set of recipients
 * beyond what can be achieved with simply a.
 * </p>
 *
 * @see Event
 * @see StreamDescriptor
 * @see Location
 */
public class RecipientSelector {
    private static final Gson gson;

    static {
        final GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }

    private final Location location;

    /**
     * RecipientSelector for specifying a message's recipient set within a Zirk's sphere(s).
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