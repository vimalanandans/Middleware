package com.bezirk.proxy.api.impl;

import com.bezirk.middleware.messages.ProtocolRole;
import com.google.gson.Gson;

import java.util.Arrays;

public class SubscribedRole extends ProtocolRole {

    private String protocolName;
    private String protocolDesc;
    private String[] eventTopics;
    private String[] streamTopics;

    public SubscribedRole() {
        //Empty constructor required for gson.fromJson
    }

    public SubscribedRole(ProtocolRole pRole) {
        protocolName = pRole.getProtocolName();
        protocolDesc = pRole.getDescription();
        eventTopics = pRole.getEventTopics();
        streamTopics = pRole.getStreamTopics();
    }

    public String getSubscribedProtocolRole() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public ProtocolRole getProtocolRole(String protocolRoleAsString) {
        Gson gson = new Gson();
        return gson.fromJson(protocolRoleAsString, SubscribedRole.class);
    }

    @Override
    public String getProtocolName() {
        return protocolName;
    }

    @Override
    public String getDescription() {
        return protocolDesc;
    }

    @Override
    public String[] getEventTopics() {
        return eventTopics == null ? null : eventTopics.clone();
    }

    @Override
    public String[] getStreamTopics() {
        return streamTopics == null ? null : streamTopics.clone();
    }

    @Override
    public String toString() {
        return " ProtocolName: " + protocolName + " ProtocolDesc: " + protocolDesc +
                " evntTopics: " + Arrays.toString(eventTopics) + " strmTopics: " +
                Arrays.toString(streamTopics);
    }
}
