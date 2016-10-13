/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.addressing;

import com.bezirk.middleware.messages.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * Aggregates addressing information that will be used for narrowing down recipients when publishing messages.
 * Typically you should think about your Zirk broadcasting messages to other Zirks within your Zirk's subnet.
 * This class allows you to filter further by using a semantic address specified by a {@link Location}.
 * <p>
 * This class is used when sending messages with
 * {@link com.bezirk.middleware.Bezirk#sendEvent(RecipientSelector, Event)}.
 * </p>
 *
 * @see Event
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
