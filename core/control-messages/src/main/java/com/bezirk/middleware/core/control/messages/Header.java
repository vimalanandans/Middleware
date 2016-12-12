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
package com.bezirk.middleware.core.control.messages;

import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.serialization.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Header contains information needed by comms to route messages
 */
public class Header {
    private static final Gson gson;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Header.class, new InterfaceAdapter<Header>());
        gson = gsonBuilder.create();
    }

    // Open Fields
    private String sphereId;
    private BezirkZirkEndPoint sender = null; // Change to ZirkEndPoint sender
    private String uniqueMsgId;
    private String eventName;
    private boolean isIdentified = false;
    private String serializedAlias;

    public Header() {
    }

    public Header(String sphereId, BezirkZirkEndPoint sender, String uniqueMsgId, String eventName) {
        this.sphereId = sphereId;
        this.sender = sender;
        this.uniqueMsgId = uniqueMsgId;
        this.eventName = eventName;
    }

    public static <C> C fromJson(String json, Class<C> objectType) {
        return gson.fromJson(json, objectType);
    }

    /**
     * @return the messageId which is generated per message sent. Every message is assigned with a
     * unique identifier
     */
    public String getUniqueMsgId() {
        return uniqueMsgId;
    }

    /**
     * @param messageId the messageId which is generated per message sent. Every message is assigned
     *                  with a unique identifier
     */
    public void setUniqueMsgId(String messageId) {
        this.uniqueMsgId = messageId;
    }

    /**
     * @return the name of the sphere
     */
    public String getSphereId() {
        return sphereId;
    }

    /**
     * @param sphereId the name of the sphere
     */
    public void setSphereId(String sphereId) {
        this.sphereId = sphereId;
    }

    /**
     * @return the senderId of the message
     */
    public BezirkZirkEndPoint getSender() {
        return sender;
    }

    /**
     * @param sender the senderId of the message. Usually there is a function that retrieves
     *               the hostId and this is used to set the senderId
     */
    public void setSender(BezirkZirkEndPoint sender) {
        this.sender = sender;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setIsIdentified(boolean isIdentified) {
        this.isIdentified = isIdentified;
    }

    public boolean isIdentified() {
        return isIdentified;
    }

    public Alias getAlias() {
        return gson.fromJson(serializedAlias, Alias.class);
    }

    public void setAlias(Alias alias) {
        this.serializedAlias = gson.toJson(alias);
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

}
