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
        //Empty constructor required for gson.fromJSON
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
        final StringBuilder builder = new StringBuilder();

        builder.append(" ProtocolName: ");
        builder.append(protocolName);
        builder.append(" ProtocolDesc: ");
        builder.append(protocolDesc);
        builder.append(" evntTopics: ");
        builder.append(Arrays.toString(eventTopics));
        builder.append(" strmTopics: ");
        builder.append(Arrays.toString(streamTopics));

        return builder.toString();
    }
}
