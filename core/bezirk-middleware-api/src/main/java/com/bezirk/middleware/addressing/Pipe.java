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

import com.google.gson.Gson;

/**
 * Represents a pipe with a name.  
 * @see CloudPipe
 */
public class Pipe {
	private String name;
	
	protected String type = getClass().getCanonicalName();
	
	public Pipe() {
		//Empty Constructor for gson.deserialize
	}
	
	/**
	 * @param pName suggested name for the pipe - which may be changed by the user via UhU UIs
	 */
	public Pipe(String pName) {
		this.name = pName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("|");
		builder.append(getClass().getSimpleName());
		builder.append(",");
		builder.append(getName());
		builder.append("|");

		return builder.toString();
	}
	
	/**
	 * @return Json representation of the message as a String.
	 */
	public String serialize() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	/**
	 * @param json The Json String that is to be deserialized
	 * @param dC class to deserialize into
	 * @return object of class C 
	 */
	public static <C> C deserialize(String json, Class cL) {
		Gson gson = new Gson();
		return (C) gson.fromJson(json, cL);
	}	
}
