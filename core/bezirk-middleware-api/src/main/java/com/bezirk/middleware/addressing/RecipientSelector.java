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
 * {@link com.bezirk.middleware.Bezirk#sendEvent(RecipientSelector, Event) events} or
 * {@link com.bezirk.middleware.Bezirk#sendStream(ZirkEndPoint, Stream, java.io.File) streams}, when
 * {@link com.bezirk.middleware.Bezirk#discover(RecipientSelector, ProtocolRole, long, int, BezirkListener) discovering}
 * Zirks, and in any other context where it is useful to narrow a message's set of recipients
 * beyond what can be achieved with simply a {@link ProtocolRole}.
 * </p>
 *
 * @see Event
 * @see com.bezirk.middleware.messages.Stream
 * @see Location
 * @see ProtocolRole
 */
public class RecipientSelector {
    private static final Gson gson;

    static {
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Pipe.class, new InterfaceAdapter<Pipe>());
        gson = builder.create();
    }

    private final Location location;
    private final Pipe pipe;

    /**
     * RecipientSelector for specifying a message's recipient set within a Zirk's sphere(s). Use
     * {@link #RecipientSelector(Location, Pipe)} if you'd like to also specify a pipe.
     *
     * @param location the semantic address used to narrow a message's set of recipients farther
     *                 than a topic does
     */
    public RecipientSelector(Location location) {
        this.location = location;
        pipe = null;
    }

    /**
     * RecipientSelector for extending a message's set of recipients to include Zirks or endpoints outside of
     * a Zirk's sphere(s) using a pipe.  Use
     * {@link #RecipientSelector(Location, Pipe)} if you'd like to also specify a semantic address.
     *
     * @param pipe the specific pipe the message should also be sent on, or <code>null</code>
     *             for all authorized pipes in the Zirk's sphere(s)
     */
    public RecipientSelector(Pipe pipe) {
        location = null;
        this.pipe = pipe;
    }

    /**
     * RecipientSelector for specifying a message's set of recipients using a semantic address and extending
     * the set of recipients to include Zirks or endpoints outside of a Zirk's sphere(s) using a
     * pipe.
     *
     * @param location the semantic address used to narrow a message's set of recipients farther than
     *                 a topic does
     * @param pipe     the specific pipe the message should also be sent on, or <code>null</code>
     *                 for all authorized pipes in the Zirk's sphere(s)
     */
    public RecipientSelector(Location location, Pipe pipe) {
        this.location = location;
        this.pipe = pipe;
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
}
