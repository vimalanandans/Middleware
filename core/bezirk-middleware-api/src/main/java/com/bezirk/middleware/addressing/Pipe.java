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

import com.google.gson.Gson;

/**
 * Represents a pipe with a name.
 *
 * @see CloudPipe
 */
public class Pipe {
    protected String type = getClass().getCanonicalName();
    private String name;

    public Pipe() {
        //Empty Constructor for gson.fromJson
    }

    /**
     * @param pName suggested name for the pipe - which may be changed by the user via Bezirk UIs
     */
    public Pipe(String pName) {
        this.name = pName;
    }

    /**
     * @param json The Json String that is to be deserialized
     * @param cL   class to fromJson into
     * @return object of class C
     */
    public static <C> C deserialize(String json, Class cL) {
        Gson gson = new Gson();
        return (C) gson.fromJson(json, cL);
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
        return "|" + getClass().getSimpleName() + "," + getName() + "|";
    }

    /**
     * @return Json representation of the message as a String.
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
