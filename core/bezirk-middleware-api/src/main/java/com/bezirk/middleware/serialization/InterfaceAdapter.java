package com.bezirk.middleware.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    @Override
    public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
        final JsonObject wrapper = new JsonObject();
        wrapper.addProperty("type", object.getClass().getName());
        wrapper.add("data", new Gson().toJsonTree(object));
        return wrapper;
    }

    @Override
    public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject wrapper = (JsonObject) elem;
        final JsonElement typeName = get(wrapper, "type");
        final JsonElement data = get(wrapper, "data");
        final Type actualType = typeForName(typeName);
        return new Gson().fromJson(data, actualType);
    }

    private Type typeForName(final JsonElement typeElem) {
        try {
            return Class.forName(typeElem.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    private JsonElement get(final JsonObject wrapper, String memberName) {
        final JsonElement elem = wrapper.get(memberName);
        if (elem == null)
            throw new JsonParseException("no '" + memberName + "' member found in what was expected to be an interface wrapper");
        return elem;
    }


}