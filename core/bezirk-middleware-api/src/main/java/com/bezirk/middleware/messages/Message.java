package com.bezirk.middleware.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import java.io.Serializable;

/**
 * Base class for all message types Zirks may exchange using the Bezirk middleware. This class
 * implements the serialization routines required to toJson/fromJson a message for
 * transfer/reception.
 */
public abstract class Message implements Serializable {
    private static final long serialVersionUID = 448363597771246254L;
    private static Gson gson;
    private String msgId;


    //private static final long serialVersionUID = 1L;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Message.class,
                new InterfaceAdapter<Message>());
        gson = gsonBuilder.create();
    }

    /**
     * This method should be only used if the Zirk needs to provide a custom Gson adapter via the <code>gsonBuilder</code>for deserializing its events.
     * This is especially useful if the event has abstract type(s) as its instance variables which need to be serialized.
     * It can be achieved by supplying the builder with Gson's RuntimeTypeAdapterFactory.
     * For an interface <code>Interface</code> with implementations as <code>Implementation1</code> and <code>Implementation1</code>, the builder would look something like this:
     * <pre>
     *
     *     //create gson builder
     *     GsonBuilder builder = new GsonBuilder();
     *     RuntimeTypeAdapterFactory &lt;Interface&gt; interfaceAdapter = RuntimeTypeAdapterFactory.of(Interface.class);
     *     interfaceAdapter.registerSubtype(Implementation1.class).registerSubtype(Implementation2.class);
     *     builder.registerTypeAdapterFactory(interfaceAdapter);
     *
     *     //set builder
     *     Message.setGsonBuilder(builder); //ensure the builder is set before sending/receiving bezirk events
     *
     * </pre>
     *
     * @param gsonBuilder gson builder with registered adapter
     */
    public static synchronized void setGsonBuilder(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(Message.class,
                new InterfaceAdapter<Message>());
        gson = gsonBuilder.create();
    }

    /**
     * Get the ID of this message to identify the conversations it is apart of.
     * <p>
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
     * Deserialize the <code>json</code> string to create an object of type <code>Message</code>.
     * This method is used by the Middleware to prepare a message for the appropriate
     * <code>BezirkListener</code> callback when a message is received.
     *
     * @param json the JSON String that is to be deserialized
     * @return an object of type <code>Message</code> deserialized from <code>json</code>
     */
    public static Message fromJson(String json) {
        return gson.fromJson(json, Message.class);
    }

    /**
     * Serialize the message to a JSON string. This method is used by the Middleware to prepare a
     * message to be sent.
     *
     * @return JSON representation of the message
     */
    public String toJson() {
        return gson.toJson(this, Message.class);
    }

    private static class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("type", new JsonPrimitive(src.getClass().getName()));
            result.add("properties", gson.toJsonTree(src, src.getClass()));
            return result;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            JsonElement element = jsonObject.get("properties");

            try {
                return (T) gson.fromJson(element, Class.forName(type));
            } catch (ClassNotFoundException cnfe) {
                throw new JsonParseException("Unknown element type: " + type, cnfe);
            }
        }
    }
}