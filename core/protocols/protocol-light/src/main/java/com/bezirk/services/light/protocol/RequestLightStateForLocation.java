package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.Event;

public class RequestLightStateForLocation extends Event {
    public static final String TOPIC = RequestLightStateForLocation.class.getSimpleName();
    private String aip_Id = "";

    private String requestedLocation;

    public RequestLightStateForLocation() {
        super(Flag.NOTICE, TOPIC);
        // TODO Auto-generated constructor stub
    }


    public String getId() {
        return aip_Id;
    }


    public void setId(String aip_Id) {
        this.aip_Id = aip_Id;
    }


    public String getRequestedLocation() {
        return requestedLocation;
    }

    public void setRequestedLocation(String requestedLocation) {
        this.requestedLocation = requestedLocation;
    }


}
