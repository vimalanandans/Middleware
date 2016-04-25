package com.bezirk.protocols.specific.userproxy;


import com.bezirk.middleware.messages.Event;

public class ClearData extends Event {

    /**
     * topic
     */
    public static final String topic = "clear-user-details";

    /**
     * Clear Observation DB
     */
    private boolean clearObservation;

    /**
     * Clear Preference DB
     */
    private boolean clearPreference;

    /**
     * User Name for which the DB needs to be cleared
     */
    private String user;

    /**
     * @param stripe
     * @param topic
     */
    public ClearData() {
        super(Flag.NOTICE, topic);

    }

    /**
     * Use instead of the generic UhuMessage.fromJSON()
     *
     * @param json
     * @return ClearData
     */
    public static ClearData deserialize(String json) {
        return Event.fromJSON(json, ClearData.class);
    }

    public boolean isClearObservation() {
        return clearObservation;
    }

    public void setClearObservation(boolean clearObservation) {
        this.clearObservation = clearObservation;
    }

    public boolean isClearPreference() {
        return clearPreference;
    }

    public void setClearPreference(boolean clearPreference) {
        this.clearPreference = clearPreference;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
