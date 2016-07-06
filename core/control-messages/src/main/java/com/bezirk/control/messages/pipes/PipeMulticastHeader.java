package com.bezirk.control.messages.pipes;

import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.serialization.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PipeMulticastHeader extends PipeHeader {
    private RecipientSelector recipientSelector;

    public static <C> C deserialize(String json, Class<C> clazz) {
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Pipe.class, new InterfaceAdapter<Pipe>());
        Gson gson = builder.create();
        return gson.fromJson(json, clazz);
    }

    public com.bezirk.control.messages.MulticastHeader toMulticastHeader() {
        com.bezirk.control.messages.MulticastHeader multicastHeader = new com.bezirk.control.messages.MulticastHeader();
        multicastHeader.setRecipientSelector(this.getRecipientSelector());
        multicastHeader.setSenderSEP(this.getSenderSEP());
        multicastHeader.setTopic(this.getTopic());

        // TODO: Do these need to be set??
        //multicastHeader.setMessageId(?);
        //multicastHeader.setSphereName(sphereName);

        return multicastHeader;
    }

    public RecipientSelector getRecipientSelector() {
        return recipientSelector;
    }

    public void setRecipientSelector(RecipientSelector recipientSelector) {
        this.recipientSelector = recipientSelector;
    }

    public String serialize() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Pipe.class, new InterfaceAdapter<Pipe>());
        Gson gson = builder.create();
        return gson.toJson(this);
    }
}
