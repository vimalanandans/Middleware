package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.UhuServiceId;
import com.google.gson.Gson;

/**
 * Base class of the Callback messages used in Uhu.
 */
public class ServiceIncomingMessage {
    /**
     * Discriminator for the Callbacks
     */
    public String callbackDiscriminator = null;

    /**
     * Recipient of this msg
     */
    protected UhuServiceId recipient;

    /**
     * @param json The Json String that is to be deserialized
     * @param dC   class to deserialize into
     * @return object of class C
     */
    public static <C> C deserialize(String json, Class<C> dC) {
        Gson gson = new Gson();
        return (C) gson.fromJson(json, dC);
    }

    /**
     * @return Json representation of the message as a String.
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getCallbackType() {
        return callbackDiscriminator;
    }

    public UhuServiceId getRecipient() {
        return recipient;
    }
}
