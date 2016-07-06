package com.bezirk.protocols.policy;


import com.bezirk.middleware.messages.Event;

public class UserProfileDistributionConfirmation extends Event {

    public static final String topic = UserProfileDistributionConfirmation.class.getSimpleName();

    public UserProfileDistributionConfirmation() {
        super(Flag.REQUEST, topic);
    }

    private String requestingService;

    private String uuid;

    public String getRequestingService() {
        return requestingService;
    }

    public void setRequestingService(String requestingService) {
        this.requestingService = requestingService;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public static UserProfileDistributionConfirmation deserialize(String json) {
        return Event.fromJson(json, UserProfileDistributionConfirmation.class);
    }
}
