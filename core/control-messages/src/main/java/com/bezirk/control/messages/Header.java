package com.bezirk.control.messages;

import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.serialization.InterfaceAdapter;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Header contains information needed by comms to route messages
 */
public class Header {
    // Open Fields
    private String sphereId = null; //Don't touch
    private BezirkZirkEndPoint sender = null; // Change to ZirkEndPoint sender
    private String uniqueMsgId = null;
    private String eventName = null;
    private boolean isIdentified = false;
    private String serializedAlias;

    private static final Gson gson;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Header.class, new InterfaceAdapter<Header>());
        gson = gsonBuilder.create();
    }

    public Header() {
    }

    public Header(String sphereId, BezirkZirkEndPoint sender, String uniqueMsgId, String eventName) {
        this.sphereId = sphereId;
        this.sender = sender;
        this.uniqueMsgId = uniqueMsgId;
        this.eventName = eventName;
    }

    /**
     * @return the messageId which is generated per message sent. Every message is assigned with a unique identifier
     */
    public String getUniqueMsgId() {
        return uniqueMsgId;
    }

    /**
     * @param messageId the messageId which is generated per message sent. Every message is assigned with a unique identifier
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

    public  String getEventName(){return eventName;}

    public  void setEventName(String eventName){this.eventName = eventName;}


    /**
     * @param sender the senderId of the message. Usually there is a function that retrieves
     *                  the hostId and this is used to set the senderId
     */
    public void setSender(BezirkZirkEndPoint sender) {
        this.sender = sender;
    }

    public void setIsIdentified(boolean isIdentified) {
        this.isIdentified = isIdentified;
    }

    public boolean isIdentified() {
        return isIdentified;
    }

    public void setAlias(Alias alias) {
        this.serializedAlias = gson.toJson(alias);
    }

    public Alias getAlias() {
        return gson.fromJson(serializedAlias, Alias.class);
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static <C> C fromJson(String json, Class<C> objectType) {
        return gson.fromJson(json, objectType);
    }

}
