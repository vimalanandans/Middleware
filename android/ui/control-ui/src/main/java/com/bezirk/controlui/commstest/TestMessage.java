package com.bezirk.controlui.commstest;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;

/**
 * Created by vnd2kor on 12/10/2015.
 * TODO : Move this common
 */
public class TestMessage {
    String MessageType;

    /**
     * @param json               The Json String that is to be deserialized
     * @param classToDeserialize class to fromJson into
     * @return object of class C
     */
    public static <C> C deserialize(String json, Class<C> classToDeserialize) throws JsonParseException {
        Gson gson = new Gson();
        return (C) gson.fromJson(json, classToDeserialize);
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    /**
     * @return Json representation of the message as a String.
     */
    public String serialize() throws IOException {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}