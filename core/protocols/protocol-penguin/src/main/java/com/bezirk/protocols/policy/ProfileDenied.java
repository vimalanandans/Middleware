package com.bezirk.protocols.policy;


import com.bezirk.middleware.messages.Event;

public class ProfileDenied extends Event {

    public static final String topic = ProfileDenied.class.getSimpleName();

    public ProfileDenied() {
        super(Flag.REPLY, topic);
    }

    private String user;
    private String reason;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public static ProfileDenied deserialize(String json) {
        return Event.fromJson(json, ProfileDenied.class);
    }

}
