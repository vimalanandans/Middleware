package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.ZirkId;
import com.google.gson.Gson;

/**
 * Base class of the Callback messages used in Bezirk.
 */
public class ServiceIncomingMessage {
    /**
     * Discriminator for the Callbacks
     */
    public String callbackDiscriminator = null;

    /**
     * Recipient of this msg
     */
    protected ZirkId recipient;

    /**
     * @param json The Json String that is to be deserialized
     * @param dC   class to fromJson into
     * @return object of class C
     */
    public static <C> C deserialize(String json, Class<C> dC) {
        final Gson gson = new Gson();
        return gson.fromJson(json, dC);
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

    public ZirkId getRecipient() {
        return recipient;
    }
}
