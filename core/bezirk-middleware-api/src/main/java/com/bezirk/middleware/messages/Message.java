package com.bezirk.middleware.messages;

import com.bezirk.middleware.BezirkListener;
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

/**
 * Base class for all message types Zirks may exchange using the Bezirk middleware. This class
 * implements the serialization routines required to toJson/fromJson a message for
 * transfer/reception.
 */
public abstract class Message {
    private static final Gson gson;

    /**
     * Hint about how the sender expects the recipient(s) to handle the message. This is typically
     * set by the concrete implementation class's constructor.
     *
     * @see #topic
     * @see Message.Flag
     */
    public final Flag flag;

    /**
     * The pub-sub topic for this message. Topics are defined by
     * {@link com.bezirk.middleware.messages.ProtocolRole ProtocolRoles}. A Zirk subscribes to
     * certain topics by using
     * {@link com.bezirk.middleware.Bezirk#subscribe(ProtocolRole, BezirkListener)} to
     * subscribe to a role. When the Bezirk middleware receives a message, it forwards that
     * message on to any registered Zirk that is subscribed to the role defining the topic.
     * The concrete implementation of a message specifies the topic, which should usually be the
     * implementation class's simple name. For example:
     * <pre>
     * public class TemperatureReadEvent implements Event {
     *     public TemperatureReadEvent() {
     *         super(Flag.NOTICE, TemperatureReadEvent.class.getSimpleName());
     *     }
     * }
     * </pre>
     */
    public final String topic;

    /**
     * Intended to help Zirks match messages with {@link #flag flags} set to {@link Message.Flag#REQUEST}
     * with their corresponding {@link Message.Flag#REPLY reply} when the reply is received by a
     * {@link BezirkListener}. The middleware does not use this property internally.
     * The Zirk sending the request should set this ID, and the responding Zirk should echo it in
     * the corresponding reply.
     * <p>
     * This property may be ignored for notices.
     * </p>
     */
    public String msgId;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Message.class, new MessageAdapter());
        gson = gsonBuilder.create();
    }

    public Message(Flag flag, String topic) {
        this.flag = flag;
        this.topic = topic;
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

    /**
     * Provides the message's recipient(s) with an indication of the intent of the
     * message and their duty to reply.
     */
    public enum Flag {
        /**
         * Indicate to the recipient(s) that the message does not require a reply.
         */
        NOTICE,
        /**
         * Indicate to the recipient(s) that the message expects a reply.
         */
        REQUEST,
        /**
         * Indicate to the recipient(s) that the message is a reply to a <code>REQUEST</code>.
         */
        REPLY
    }

    private static class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {
        private static final Gson gson;

        static {
            gson = new Gson();
        }

        @Override
        public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("type", new JsonPrimitive(src.getClass().getName()));
            result.add("properties", gson.toJsonTree(src, src.getClass()));
            return result;
        }

        @Override
        public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            JsonElement element = jsonObject.get("properties");

            try {
                return (Message) gson.fromJson(element, Class.forName(type));
            } catch (ClassNotFoundException cnfe) {
                throw new JsonParseException("Unknown element type: " + type, cnfe);
            }
        }
    }
}
