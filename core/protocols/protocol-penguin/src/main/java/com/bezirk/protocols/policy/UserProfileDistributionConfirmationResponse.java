package com.bezirk.protocols.policy;


import com.bezirk.middleware.messages.Event;

public class UserProfileDistributionConfirmationResponse extends Event {

    public static final String topic = UserProfileDistributionConfirmationResponse.class.getSimpleName();

    public UserProfileDistributionConfirmationResponse() {
        super(Flag.REPLY, topic);
    }

    private Boolean allowDistributionOfUserProfile;
    private String requestingService;
    private Boolean persistDetails;
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getPersistDetails() {
        return persistDetails;
    }

    public void setPersistDetails(Boolean persistDetails) {
        this.persistDetails = persistDetails;
    }

    public Boolean getAllowDistributionOfUserProfile() {
        return allowDistributionOfUserProfile;
    }

    public void setAllowDistributionOfUserProfile(
            Boolean allowDistributionOfUserProfile) {
        this.allowDistributionOfUserProfile = allowDistributionOfUserProfile;
    }

    public static UserProfileDistributionConfirmationResponse deserialize(String json) {
        return Event.fromJson(json, UserProfileDistributionConfirmationResponse.class);
    }

    public String getRequestingService() {
        return requestingService;
    }

    public void setRequestingService(String requestingService) {
        this.requestingService = requestingService;
    }

}
