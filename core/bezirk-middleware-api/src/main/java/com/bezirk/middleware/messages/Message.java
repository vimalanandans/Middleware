package com.bezirk.middleware.messages;

import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.serialization.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * Base class for all message types Zirks may exchange using the Bezirk middleware. This class
 * implements the serialization routines required to toJson/fromJson a message for
 * transfer/reception.
 */
public abstract class Message implements Serializable {
    private static final Gson gson;
    private String msgId;

    //defualt value
    private static final long serialVersionUID = 1L;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Message.class,
                new InterfaceAdapter<Message>(Alias.class, new InterfaceAdapter<Alias>()));
        gson = gsonBuilder.create();
    }

    /**
     * Get the ID of this message to identify the conversations it is apart of.
     *
     * The message ID is intended to help Zirks match messages that are making a request with their
     * reply when the reply is received. The middleware does not use this property internally. The
     * Zirk sending the request should set this ID, and the responding Zirk should echo it in the
     * corresponding reply.
     *
     * @param messageId the ID used to identify the conversation this message is a part ofs
     */
    public void setMessageId(String messageId) {
        msgId = messageId;
    }

    public String getMessageId() {
        return msgId;
    }

    /**
     * Deserialize the <code>json</code> string to create an object of type <code>objectType</code>.
     * This method is used by the Middleware to prepare a message for the appropriate
     * <code>BezirkListener</code> callback when a message is received.
     *
     * @param <C>        the type of the object represented by <code>json</code>, set by
     *                   <code>objectType</code>
     * @param json       the JSON String that is to be deserialized
     * @param objectType the type of the object represented by <code>json</code>
     * @return an object of type <code>objectType</code> deserialized from <code>json</code>
     */
    public static <C> C fromJson(String json, Class<C> objectType) {
        return gson.fromJson(json, objectType);
    }

    /**
     * Serialize the message to a JSON string. This method is used by the Middleware to prepare a
     * message to be sent.
     *
     * @return JSON representation of the message
     */
    public String toJson() {
        return gson.toJson(this);
    }
}