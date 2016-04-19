/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 *
 * Authors: Joao de Sousa, 2014
 *          Mansimar Aneja, 2014
 *          Vijet Badigannavar, 2014
 *          Samarjit Das, 2014
 *          Cory Henson, 2014
 *          Sunil Kumar Meena, 2014
 *          Adam Wynne, 2014
 *          Jan Zibuschka, 2014
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
 *
 * <h4>Scopes</h4>
 * To identify a Thing or set of Things, a semantic address contains three scopes of 
 * increasing specificity that resolve to place a specific Thing or Things in scope:
 *
 * <ul>
 *   <li>A <em>wide scope</em> specifies a potentially large set of Things. For example, 
 *   the floor of a building (e.g. &quot;floor 1&quot;) represents a wide scope.</li>
 *   <li>An <em>intermediate scope</em> refines a wide scope to further target Things of 
 *   interest. Building on the previous example, specifying a room on floor 1 (e.g. 
 *   &quot;kitchen&quot;) represents an intermediate scope.</li>
 *   <li>A <em>narrow scope</em> further refines the set of Things to just the specific 
 *   Thing or Things that will be the message recipients. Again building on the previous 
 *   example, specifying a specific light (e.g. "ceiling light") or area of the kitchen 
 *   (e.g. &quot;window&quot;) completes the semantic address.</li>
 * </ul>
 *
 * <mark><strong>TODO</strong></mark>: We need to say something about where these names 
 * (e.g. ceiling light) come from.
  
 * <h4>Representing Semantic Addresses as Strings</h4>
 * Semantic addresses are represented as strings by listing each scope in descending order 
 * separated by a forward slash: <code>"wide scope/intermediate scope/narrow scope"</code>. 
 * For example, using the scopes in the examples from the previous section, the semantic 
 * addresses are represented by the following strings: 
 * <code>"floor 1/kitchen/ceiling light"</code> and <code>"floor 1/kitchen/window"</code>.
 *
 * <h4>Specifying Scopes</h4>
 * The relative size of each scope is dependent on the specific context the semantic address 
 * is used in. The previous examples were within the context of Things in a building, however 
 * in a context where Things are traffic controls for municipalities the wide scope may be a 
 * city, the intermediate scope a street, and the narrow scope a pedestrian walk sign or set 
 * of traffic lights.
 * <p>
 * Each scope is optional. If you do not specify any scope you are referring to all Things in 
 * a sphere subscribed to the message's role. This is equivalent to not using a semantic 
 * address. However, any of the scopes may be skipped if necessary to define the desired set 
 * of Things. Once again using the building example, the following addresses refer to different 
 * sets of Things whose services are within a sphere subscribed to the targeted protocol role:
 *
 * <ul>
 *	 <li><code>"floor1//"</code> refers to all Things on floor 1.</li>
 *	 <li><code>"floor1/kitchen/"</code> refers to all Things in floor 1's kitchen.</li>
 *	 <li><code>"floor1/kitchen/window"</code> refers to all Things in floor 1's kitchen 
 *   window or the window itself.</li>
 *   <li><code>"floor 1//light"</code> refers to all Things named &quot;light&quot; on 
 *   floor 1.</li> 
 *	 <li><code>"/kitchen/light"</code> refers to all Things named &quot;light&quot; in 
 *   rooms named &quot;kitchen&quot; in the building</li> 
 *	 <li><code>"//light"</code> refers to all things named &quot;light&quot; in the 
 *   building.</li> 
 * </ul>
 *
 * <h4>Practical Example</h4>
 * <mark><strong>TODO</strong></mark>: We need to show some code here.
 */
public class Location implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String region;
	private String in;
	private String near;

	/**
	 * A location defined by a region, an area within the region, and proximity to a landmark object; any or all of which may be NULL.
	 * 
	 * @param region  for example "floor1" or "Pennsylvania". Commas and slashes are cleared from the region's name.
	 * @param in for example "bedroom" or "greater Pittsburgh". Commas and slashes are cleared from the area's name.
	 * @param near for example "bed" or "Frick park". Commas and slashes are cleared from the landmark's name.
	 */
	public Location(String region, String in, String near) {
		this.region = (region==null)? null : region.replace(",","").replace("/", "");
		this.in = (in==null)? null :in.replace(",","").replace("/", "");
		this.near = (near==null)? null :near.replace(",","").replace("/", "");
	}

	/**
	 * Constructs a Location from its string representation.
	 * @param location with attributes separated by '/', passing null is equivalent to Location(null,null,null)
	 * @see #toString()
	 */
	public Location(String location) {
		region = null;
		in = null;
		near = null;

		if(location != null){
			if(location.contains("/")) {
				setLocationParams(location);
			} else {
				region = location;
			}
		}

	}


	private void setLocationParams(String location){
		String[] attributes = location.split("/");
		switch (attributes.length){
		case 1:
			region = computeParam(attributes[0]);
			break;
		case 2:
			region = computeParam(attributes[0]);
			in = computeParam(attributes[1]);
			break;
		case 3:
			region = computeParam(attributes[0]);
			in = computeParam(attributes[1]);
			near = computeParam(attributes[2]);
			break;
		default:
			break;
		}
	}

	private String computeParam(String input){
		return input==null || "null".equals(input) || input.isEmpty()?null:input;
	}
	/**
	 * @return region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @return area in the region
	 */
	public String getArea() {
		return in;
	}

	/**
	 * @return proximate landmark object
	 */
	public String getLandmark() {
		return near;
	}

	/**
	 * L1 subsumes L2 if, for all attributes (region, in, near)
	 * - L1.attribute equals L2.attribute, or
	 * - L1.attribute is open (NULL) and L2.attribute is specific.
	 * 
	 * UhU uses the subsumes relation to match the address of an incoming message M to the location of a service S.
	 * As far as location, the message is delivered if M.getLocation().subsumes(S.getLocation())
	 * 
	 * @param loc
	 * @return whether location is subsumed by this
	 */
	public boolean subsumes(Location loc) {
		return matchParam(region, loc.region) && matchParam(in, loc.in) && matchParam(near, loc.near);		
	}

	private boolean matchParam(String thisParam, String compareParam){
		return thisParam==null || thisParam.equalsIgnoreCase("null") || thisParam.equalsIgnoreCase(compareParam);
	}

	/**
	 * @return string representation with attributes separated by '/'
	 * @see #Location(String)
	 */
	public String toString(){
		return String.valueOf(this.region)+"/"+String.valueOf(this.in)+"/"+String.valueOf(this.near);	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((in == null) ? 0 : in.hashCode());
		result = prime * result + ((near == null) ? 0 : near.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
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
		if (in == null) {
			if (other.in != null)
				return false;
		} else if (!in.equals(other.in))
			return false;
		if (near == null) {
			if (other.near != null)
				return false;
		} else if (!near.equals(other.near))
			return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		return true;
	}
	
	
	

}
