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

import com.bezirk.middleware.messages.MessageSet;

import java.io.Serializable;

/**
 * A <code>Location</code> represents a <em>semantic address</em> that specifies the
 * physical location of a Thing or a set of Things. Typically, messages are broadcast
 * within a subnet and filtered per-Zirk based on the messages in the various
 * {@link MessageSet MessageSets} each Zirk is subscribed to, which may be too coarse-grained
 * for some cases. A semantic address is used to more precisely scope message recipients
 * after message-based filtering occurs.
 * <h1>Scopes</h1>
 * <p>
 * To spatially select a Thing or set of Things, a semantic address contains three levels of
 * spatial specificity:
 * </p>
 * <ul>
 * <li>A <em>wide scope</em> denotes a large spatial area. For example,
 * the floor of a building (e.g. &quot;floor 1&quot;) represents a wide scope, which can
 * contain many Things.</li>
 * <li>An <em>intermediate scope</em> denotes a smaller spatial area. Building on the previous
 * example, specifying a room on floor 1 (e.g. &quot;kitchen&quot;) represents an intermediate
 * scope. There are usually fewer things in an intermediate scope than in a wide scope. However,
 * you can specify an intermediate scope without a wide scope; for example, specifying
 * &quot;kitchen&quot; without &quot;floor 1&quot; would select all the kitchens in a Zirk's
 * subnet, which may include several floors. In this case, you could potentially be selecting
 * more things.</li>
 * <li>A <em>narrow scope</em> denotes the smallest spatial area. Again building on the previous
 * example, specifying a specific part of the kitchen (e.g. &quot;window&quot; or
 * &quot;cake prep&quot;) completes the semantic address.</li>
 * </ul>
 * <p>
 * The actual names of scopes can be specified manually by the user or automatically by a service.
 * For example, a user that connects a light Zirk to a new light may be prompted to enter the
 * location of the light as a string. If the user is using a Zirk that provides location awareness
 * as a service, the names may instead be set by the location Zirk. The location of a Zirk
 * operating a Thing is set using {@link com.bezirk.middleware.Bezirk#setLocation(Location)}.
 * </p>
 * <h1>Representing Semantic Addresses as Strings</h1>
 * Semantic addresses are represented as strings by listing each scope in descending order
 * separated by a forward slash: <code>"wide scope/intermediate scope/narrow scope"</code>.
 * For example, using the scopes in the examples from the previous section, the semantic
 * addresses are represented by the following strings:
 * <code>"floor 1/kitchen/window"</code> and <code>"floor 1/kitchen/cake prep"</code>.
 * <h1>Specifying Scopes</h1>
 * The relative size of each scope is dependent on the specific spatial context surrounding the
 * semantic address. The previous examples were within the context of Things in a building; however
 * in a context where Things are traffic controls for municipalities, for example, the wide scope
 * may be a city, the intermediate scope a street, and the narrow scope a building number or
 * intersection.
 * <p>
 * Each scope is optional. If you do not specify any scope you are referring to all Things in
 * a subnet subscribed to the message's topic. This is equivalent to not using a semantic
 * address. However, any of the scopes may be skipped if necessary to define the desired set
 * of Things. Once again using the building example, the following addresses refer to different
 * sets of Things whose Zirks are within a subnet subscribed to the targeted topic:
 * </p>
 * <ul>
 * <li><code>"floor1//"</code> refers to all Things on floor 1.</li>
 * <li><code>"floor1/kitchen/"</code> refers to all Things in floor 1's kitchen.</li>
 * <li><code>"floor1/kitchen/window"</code> refers to all Things in floor 1's kitchen
 * window.</li>
 * <li><code>"floor 1//cake prep"</code> refers to all Things in an any area called &quot;cake prep&quot;
 * on floor 1.</li>
 * <li><code>"/kitchen/cake prep"</code> refers to all Things in an area called &quot;cake prep&quot;
 * in rooms named &quot;kitchen&quot; anywhere in the building in the sending Zirk's subnet</li>
 * <li><code>"//cake prep"</code> refers to all things in any area named &quot;cake prep&quot; in the
 * sending Zirk's subnet.</li>
 * </ul>
 * <h1>Practical Example</h1>
 * Setting a Thing's physical location during initial configuration is a typical reason to use
 * a semantic address. The exact contents of this address can come from user-input during a Zirk's
 * initial connection to the Thing, or from a Zirk providing location awareness services. In
 * practice, such a location-awareness Zirk will typically be trained beforehand to know the names
 * of each scope in some location (e.g. the names of floors and rooms on those floors). These names
 * can later be used to provide parameters for a <code>Location</code>. In this scenario, a Zirk first
 * connecting to a Thing will query the location service Zirk asking for the current location
 * and will use the reply to set starter wide, intermediate, and narrow scopes. The user can then
 * choose to override the initial values for any scope.
 * <pre>
 *     // ...
 *
 *     EventSet e = new EventSet(UserLocationEvent.class);
 *
 *     e.setEventReceiver((event, sender) -&gt; {
 *         final UserLocationEvent locationEvent = (UserLocationEvent) event;
 *         final Location userLocation = locationEvent.getLocation();
 *
 *         // Create a semantic address referring to all Things in the user's current
 *         // room
 *         final Location lightLocation = new Location(userLocation.getWideScope(),
 *                                               userLocation.getIntermediateScope(),
 *                                               null);
 *
 *         // Send an event to all Things at the semantic address subscribed to the
 *         // light protocol telling the Things to turn on
 *         bezirk.sendEvent(new RecipientSelector(lightLocation), new TurnLightOnEvent());
 *     });
 *
 *     // ...
 * </pre>
 */
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    private String wideScope;
    private String intermediateScope;
    private String narrowScope;

    /**
     * A location defined by a <code>wideScope</code>, an <code>intermediateScope</code> within the
     * wide scope, and a <code>narrowScope</code> within the intermediate scope. Any or all of the
     * scopes may be <code>null</code>.
     *
     * @param wideScope         for example "floor1" or "Pennsylvania". Commas and slashes are
     *                          cleared from the wideScope's name.
     * @param intermediateScope for example "bedroom" or "greater Pittsburgh". Commas and slashes
     *                          are cleared from the area's name.
     * @param narrowScope       for example "closet" or "Frick park". Commas and slashes are cleared
     *                          from the landmark's name.
     */
    public Location(String wideScope, String intermediateScope, String narrowScope) {
        this.wideScope = (wideScope == null) ? null : wideScope.replace(",", "").replace("/", "");
        this.intermediateScope = (intermediateScope == null) ? null :
                intermediateScope.replace(",", "").replace("/", "");
        this.narrowScope = (narrowScope == null) ? null : narrowScope.replace(",", "").replace("/", "");
    }

    /**
     * Construct a Location from its string representation. The string is in the format:
     * <code>"wide scope/intermediate scope/narrow scope"</code>.
     *
     * @param location scopes separated by '/'. Throws an <code>IllegalArgumentException</code> if
     *                 <code>location</code> is <code>null</code>.
     */
    public Location(String location) {
        if (location == null) {
            throw new IllegalArgumentException("You must specify a string representing a semantic " +
                    "address if you are going to use a Location. If you simply want to broadcast" +
                    "an event, use Bezirk.sendEvent(Event e).");
        } else {
            if (location.contains("/")) {
                setLocationParams(location);
            } else {
                wideScope = location;
            }
        }

    }

    /**
     * Set this <code>Location</code>'s semantic address using a string in the format:
     * <code>"wide scope/intermediate scope/narrow scope"</code>.
     *
     * @param location this location's new semantic address specified as a string
     */
    private void setLocationParams(String location) {
        final String[] attributes = location.split("/");
        switch (attributes.length) {
            case 1:
                wideScope = computeParam(attributes[0]);
                break;
            case 2:
                wideScope = computeParam(attributes[0]);
                intermediateScope = computeParam(attributes[1]);
                break;
            case 3:
                wideScope = computeParam(attributes[0]);
                intermediateScope = computeParam(attributes[1]);
                narrowScope = computeParam(attributes[2]);
                break;
            default:
                break;
        }
    }

    private String computeParam(String input) {
        return input == null || "null".equals(input) || input.isEmpty() ? null : input;
    }

    /**
     * Return the largest scope that helps resolve this <code>Location</code>'s set of Things.
     *
     * @return the largest scope that helps resolve this <code>Location</code>'s set of Things
     */
    public String getWideScope() {
        return wideScope;
    }

    /**
     * Return the middle scope that helps resolve this <code>Location</code>'s set of Things.
     *
     * @return the middle scope that helps resolve this <code>Location</code>'s set of Things
     */
    public String getIntermediateScope() {
        return intermediateScope;
    }

    /**
     * Return the narrowest scope that helps resolve this <code>Location</code>'s set of Things.
     *
     * @return the narrowest scope that helps resolve this <code>Location</code>'s set of Things
     */
    public String getNarrowScope() {
        return narrowScope;
    }

    /**
     * L1 subsumes L2 if, for all attributes (wideScope, intermediateScope, narrowScope)
     * - L1.attribute equals L2.attribute, or
     * - L1.attribute is open (NULL) and L2.attribute is specific.
     * <p>
     * Bezirk uses the subsumes relation to match the address of an incoming message M to the
     * location of a Zirk Z. As far as location, the message is delivered
     * if <code>M.getLocation().subsumes(S.getLocation()) == true</code>.
     * </p>
     *
     * @param location the location that may be subsumed by this
     * @return whether location is subsumed by this
     */
    public boolean subsumes(Location location) {
        return matchParam(wideScope, location.wideScope) &&
                matchParam(intermediateScope, location.intermediateScope) &&
                matchParam(narrowScope, location.narrowScope);
    }

    private boolean matchParam(String thisParam, String compareParam) {
        return thisParam == null || thisParam.equalsIgnoreCase("null") ||
                thisParam.equalsIgnoreCase(compareParam);
    }

    /**
     * Convert this location to a <code>String</code> in the format:
     * <code>"wide scope/intermediate scope/narrow scope"</code>.
     *
     * @return this <code>Location</code> represented as a string where each scope is separated by
     * '/'
     */
    public String toString() {
        return String.valueOf(this.wideScope) + "/" + String.valueOf(this.intermediateScope) + "/" +
                String.valueOf(this.narrowScope);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((intermediateScope == null) ? 0 : intermediateScope.hashCode());
        result = prime * result + ((narrowScope == null) ? 0 : narrowScope.hashCode());
        result = prime * result + ((wideScope == null) ? 0 : wideScope.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Location other = (Location) obj;
        if (intermediateScope == null) {
            if (other.intermediateScope != null)
                return false;
        } else if (!intermediateScope.equals(other.intermediateScope))
            return false;
        if (narrowScope == null) {
            if (other.narrowScope != null)
                return false;
        } else if (!narrowScope.equals(other.narrowScope))
            return false;
        if (wideScope == null) {
            if (other.wideScope != null)
                return false;
        } else if (!wideScope.equals(other.wideScope))
            return false;
        return true;
    }
}
