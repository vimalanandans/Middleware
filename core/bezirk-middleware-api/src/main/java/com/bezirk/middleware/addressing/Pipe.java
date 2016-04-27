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
import com.google.gson.Gson;

/**
 * <p>
 * <b style="color:darkred;">This JavaDoc is for an API class that is currently incomplete.</b>
 * </p>
 * A pipe is a user-authorized communication channel used to send messages from a Zirk
 * to a non-Bezirk endpoint on the Internet or between one Zirk and another Zirk in a separate
 * sphere (i.e. pipes allow Zirks that do not share spheres to communicate). Pipes have security
 * policies associated with them, where the policies are instantiations of
 * {@link PipePolicy}. To initiate the authorization process, a Zirk must call
 * {@link com.bezirk.middleware.Bezirk#requestPipeAuthorization(ZirkId, Pipe, PipePolicy, PipePolicy, BezirkListener)}.
 * If you attempt to use a pipe that has not been authorized, a <code>java.lang.SecurityException</code>
 * will be thrown.
 */
public class Pipe {
    protected String type = getClass().getSimpleName();
    private String name;

    private static final Gson gson = new Gson();

    public Pipe() {
        //Empty ctor for gson.fromJson
    }

    /**
     * Creates a pipe with the user-friendly name <code>pipeName</code>. The user can change
     * this name in the Bezirk UI.
     *
     * @param pipeName suggested name for the pipe, user-changeable
     */
    public Pipe(String pipeName) {
        this.name = pipeName;
    }

    /**
     * Serialize the policy to a JSON string.
     *
     * @return JSON representation of the policy
     */
    public String toJson() {
        return gson.toJson(this);
    }

    /**
     * Deserialize the <code>json</code> string to create an object of type <code>objectType</code>.
     *
     * @param <C>        the type of the object represented by <code>json</code>, set by
     *                   <code>objectType</code>
     * @param json       the JSON String that is to be deserialized
     * @param objectType the type of the object represented by <code>json</code>
     * @return an object of type <code>objectType</code> deserialized from <code>json</code>
     */
    public static <C> C fromJson(String json, Class objectType) {
        return (C) gson.fromJson(json, objectType);
    }

    /**
     * Returns the name of this pipe.
     *
     * @return the name of this pipe
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this pipe. This is typically used when the user changes the pipe's name
     * using the Bezirk UI.
     *
     * @param name the new name for this pipe
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return "|" + getClass().getSimpleName() + "," + getName() + "|";
    }
}
