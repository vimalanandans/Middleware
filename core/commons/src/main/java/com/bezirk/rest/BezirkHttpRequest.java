package com.bezirk.rest;

public class BezirkHttpRequest {

    private String eventMsg;
    private String eventTopic;
    private String eventSphere;
    private String eventServiceId;
    private String uniqueEventId;
    private String expectedResponseType;
    private Integer uniqueID;


    public BezirkHttpRequest() {
        // TODO Auto-generated constructor stub
    }

    public String getEventMsg() {
        return eventMsg;
    }

    public void setEventMsg(String eventMsg) {
        this.eventMsg = eventMsg;
    }

    public String getEventTopic() {
        return eventTopic;
    }

    public void setEventTopic(String eventTopic) {
        this.eventTopic = eventTopic;
    }

    public String getEventSphere() {
        return eventSphere;
    }

    public void setEventSphere(String eventSphere) {
        this.eventSphere = eventSphere;
    }

    public String getExpectedResponseType() {
        return expectedResponseType;
    }

    public void setExpectedResponseType(String expectedResponseType) {
        this.expectedResponseType = expectedResponseType;
    }

    public String getEventServiceId() {
        return eventServiceId;
    }

    public void setEventServiceId(String eventServiceId) {
        this.eventServiceId = eventServiceId;
    }

    public Integer getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(Integer uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getUniqueEventId() {
        return uniqueEventId;
    }

    public void setUniqueEventId(String uniqueEventId) {
        this.uniqueEventId = uniqueEventId;
    }

}
