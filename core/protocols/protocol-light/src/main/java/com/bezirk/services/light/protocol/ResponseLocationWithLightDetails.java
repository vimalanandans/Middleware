package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.Event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ResponseLocationWithLightDetails extends Event {

    public static final String TOPIC = ResponseLocationWithLightDetails.class.getSimpleName();

    private Map<String, HashSet<String>> locToIds = new HashMap<String, HashSet<String>>();
    private String id = "";

    public ResponseLocationWithLightDetails() {
        super(Flag.REPLY, TOPIC);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, HashSet<String>> getLocToIds() {
        return locToIds;
    }

    public void setLocToIds(Map<String, HashSet<String>> locToIds) {
        this.locToIds = locToIds;
    }

}