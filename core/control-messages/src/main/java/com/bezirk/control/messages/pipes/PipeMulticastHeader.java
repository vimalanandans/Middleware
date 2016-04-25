package com.bezirk.control.messages.pipes;

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.serialization.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PipeMulticastHeader extends PipeHeader {
    private Address address;

    public static <C> C deserialize(String json, Class<C> clazz) {
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Pipe.class, new InterfaceAdapter<Pipe>());
        Gson gson = builder.create();
        return gson.fromJson(json, clazz);
    }

    public com.bezirk.control.messages.MulticastHeader toMulticastHeader() {
        com.bezirk.control.messages.MulticastHeader multicastHeader = new com.bezirk.control.messages.MulticastHeader();
        multicastHeader.setAddress(this.getAddress());
        multicastHeader.setSenderSEP(this.getSenderSEP());
        multicastHeader.setTopic(this.getTopic());

        // TODO: Do these need to be set??
        //multicastHeader.setMessageId(?);
        //multicastHeader.setSphereName(sphereName);

        return multicastHeader;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String serialize() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Pipe.class, new InterfaceAdapter<Pipe>());
        Gson gson = builder.create();
        return gson.toJson(this);
    }
}
