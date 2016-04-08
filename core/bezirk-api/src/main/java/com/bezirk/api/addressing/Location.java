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
package com.bezirk.api.addressing;

import java.io.Serializable;


/**
 * Physical location for objects of interest, such as people, devices, and services.
 * Location is structured using three containment attributes: region, area within the region (in), and proximity to a landmark object (near)
 * All attributes are optional, e.g. any area within the region.
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
