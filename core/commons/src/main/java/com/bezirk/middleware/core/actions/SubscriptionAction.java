package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.middleware.messages.StreamSet;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
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

public class SubscriptionAction extends ZirkAction {

    private static final Gson gson;
    private static final long serialVersionUID = -5818106760459744698L;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(MessageSet.class, new MessageSetAdapter());
        gson = gsonBuilder.create();
    }

    private final com.bezirk.middleware.core.actions.BezirkAction action;
    private final String serializedMessageSet;

    public SubscriptionAction(com.bezirk.middleware.core.actions.BezirkAction action, ZirkId zirkId, MessageSet messageSet) {
        super(zirkId);

        // MessageSet can be null for unsubscribing from all messages
        if (messageSet != null && messageSet.getMessages().isEmpty()) {
            throw new IllegalArgumentException("messageSet must be non-null and must subscribe to at " +
                    "least one event or stream");
        }

        this.action = action;
        serializedMessageSet = gson.toJson(messageSet);
    }

    public MessageSet getMessageSet() {
        return gson.fromJson(serializedMessageSet, MessageSet.class);
    }

    public com.bezirk.middleware.core.actions.BezirkAction getAction() {
        return action;
    }

    public static class MessageSetAdapter implements JsonSerializer<MessageSet>,
            JsonDeserializer<MessageSet> {
        private final Gson gson = new Gson();

        @Override
        public JsonElement serialize(MessageSet src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();

            if (src instanceof EventSet) {
                result.add("type", new JsonPrimitive(EventSet.class.getName()));
            } else if (src instanceof StreamSet) {
                result.add("type", new JsonPrimitive(StreamSet.class.getName()));
            } else {
                throw new AssertionError("Unknown MessageSet type: " +
                        src.getClass().getSimpleName());
            }

            result.add("properties", gson.toJsonTree(src, src.getClass()));
            return result;
        }

        @Override
        public MessageSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            JsonElement element = jsonObject.get("properties");

            try {
                return (MessageSet) gson.fromJson(element, Class.forName(type));
            } catch (ClassNotFoundException cnfe) {
                throw new JsonParseException("Unknown element type: " + type, cnfe);
            }
        }
    }
}
