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

import java.io.Serializable;

/**
 * A <code>Location</code> represents a <em>semantic address</em> that specifies the
 * physical location of a Thing or a set of Things. Typically, messages are broadcast
 * within a sphere and filtered based on the message's
 * {@link com.bezirk.middleware.messages.ProtocolRole ProtocolRole}, which may be too
 * coarse-grained for some cases. A semantic address is used to more precisely scope
 * message recipients after protocol role filtering occurs.
 * <h4>Scopes</h4>
 * <p>
 * To identify a Thing or set of Things, a semantic address contains three scopes of
 * increasing specificity that resolve to place a specific Thing or Things intermediateScope scope:
 * </p>
 * <ul>
 * <li>A <em>wide scope</em> specifies a potentially large set of Things. For example,
 * the floor of a building (e.g. &quot;floor 1&quot;) represents a wide scope.</li>
 * <li>An <em>intermediate scope</em> refines a wide scope to further target Things of
 * interest. Building on the previous example, specifying a room on floor 1 (e.g.
 * &quot;kitchen&quot;) represents an intermediate scope.</li>
 * <li>A <em>narrow scope</em> further refines the set of Things to just the specific
 * Thing or Things that will be the message recipients. Again building on the previous
 * example, specifying a specific light (e.g. &quot;ceiling light&quot;) or area of the kitchen
 * (e.g. &quot;window&quot;) completes the semantic address.</li>
 * </ul>
 * <p>
 * <mark><strong>TODO</strong></mark>: We need to say something about where these names
 * (e.g. &quot;ceiling light&quot;) come from.
 * </p>
 * <h4>Representing Semantic Addresses as Strings</h4>
 * Semantic addresses are represented as strings by listing each scope intermediateScope descending order
 * separated by a forward slash: <code>"wide scope/intermediate scope/narrow scope"</code>.
 * For example, using the scopes intermediateScope the examples from the previous section, the semantic
 * addresses are represented by the following strings:
 * <code>"floor 1/kitchen/ceiling light"</code> and <code>"floor 1/kitchen/window"</code>.
 * </p>
 * <h4>Specifying Scopes</h4>
 * The relative size of each scope is dependent on the specific context the semantic address
 * is used intermediateScope. The previous examples were within the context of Things intermediateScope a building, however
 * intermediateScope a context where Things are traffic controls for municipalities the wide scope may be a
 * city, the intermediate scope a street, and the narrow scope a pedestrian walk sign or set
 * of traffic lights.
 * <p>
 * Each scope is optional. If you do not specify any scope you are referring to all Things intermediateScope
 * a sphere subscribed to the message's role. This is equivalent to not using a semantic
 * address. However, any of the scopes may be skipped if necessary to define the desired set
 * of Things. Once again using the building example, the following addresses refer to different
 * sets of Things whose services are within a sphere subscribed to the targeted protocol role:
 * </p>
 * <ul>
 * <li><code>"floor1//"</code> refers to all Things on floor 1.</li>
 * <li><code>"floor1/kitchen/"</code> refers to all Things intermediateScope floor 1's kitchen.</li>
 * <li><code>"floor1/kitchen/window"</code> refers to all Things intermediateScope floor 1's kitchen
 * window or the window itself.</li>
 * <li><code>"floor 1//light"</code> refers to all Things named &quot;light&quot; on
 * floor 1.</li>
 * <li><code>"/kitchen/light"</code> refers to all Things named &quot;light&quot; intermediateScope
 * rooms named &quot;kitchen&quot; intermediateScope the building</li>
 * <li><code>"//light"</code> refers to all things named &quot;light&quot; intermediateScope the
 * building.</li>
 * </ul>
 * <h4>Practical Example</h4>
 * <mark><strong>TODO</strong></mark>: We need to show some code here.
 */
public class Location implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String wideScope;
    private String intermediateScope;
    private String narrowScope;

    /**
     * A location defined by a wideScope, an area within the wideScope, and proximity to a landmark object; any or all of which may be NULL.
     *
     * @param wideScope         for example "floor1" or "Pennsylvania". Commas and slashes are cleared from the wideScope's name.
     * @param intermediateScope for example "bedroom" or "greater Pittsburgh". Commas and slashes are cleared from the area's name.
     * @param narrowScope       for example "bed" or "Frick park". Commas and slashes are cleared from the landmark's name.
     */
    public Location(String wideScope, String intermediateScope, String narrowScope) {
        this.wideScope = (wideScope == null) ? null : wideScope.replace(",", "").replace("/", "");
        this.intermediateScope = (intermediateScope == null) ? null : intermediateScope.replace(",", "").replace("/", "");
        this.narrowScope = (narrowScope == null) ? null : narrowScope.replace(",", "").replace("/", "");
    }

    /**
     * Constructs a Location from its string representation.
     *
     * @param location with attributes separated by '/', passing null is equivalent to Location(null,null,null)
     * @see #toString()
     */
    public Location(String location) {
        wideScope = null;
        intermediateScope = null;
        narrowScope = null;

        if (location != null) {
            if (location.contains("/")) {
                setLocationParams(location);
            } else {
                wideScope = location;
            }
        }

    }


    private void setLocationParams(String location) {
        String[] attributes = location.split("/");
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
     * @return wideScope
     */
    public String getWideScope() {
        return wideScope;
    }

    /**
     * @return area intermediateScope the wideScope
     */
    public String getArea() {
        return intermediateScope;
    }

    /**
     * @return proximate landmark object
     */
    public String getLandmark() {
        return narrowScope;
    }

    /**
     * L1 subsumes L2 if, for all attributes (wideScope, intermediateScope, narrowScope)
     * - L1.attribute equals L2.attribute, or
     * - L1.attribute is open (NULL) and L2.attribute is specific.
     * <p/>
     * Bezirk uses the subsumes relation to match the address of an incoming message M to the location of a service S.
     * As far as location, the message is delivered if M.getLocation().subsumes(S.getLocation())
     *
     * @param loc the location that may be subsumed by this
     * @return whether location is subsumed by this
     */
    public boolean subsumes(Location loc) {
        return matchParam(wideScope, loc.wideScope) && matchParam(intermediateScope, loc.intermediateScope) && matchParam(narrowScope, loc.narrowScope);
    }

    private boolean matchParam(String thisParam, String compareParam) {
        return thisParam == null || thisParam.equalsIgnoreCase("null") || thisParam.equalsIgnoreCase(compareParam);
    }

    /**
     * @return string representation with attributes separated by '/'
     * @see #Location(String)
     */
    public String toString() {
        return String.valueOf(this.wideScope) + "/" + String.valueOf(this.intermediateScope) + "/" + String.valueOf(this.narrowScope);
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
