package com.bezirk.middleware.messages;

import com.bezirk.middleware.addressing.ZirkEndPoint;

public class MockZirkEndpoint implements ZirkEndPoint {
    private String zirkId = null;

    /**
     * @param zirkId
     */
    public MockZirkEndpoint(String zirkId) {
        super();
        this.zirkId = zirkId;
    }

    public MockZirkEndpoint() {
        super();
    }

    public String getZirkId() {
        return zirkId;
    }

    /**
     * @param zirkId the zirkId to set
     */
    public void setZirkId(String zirkId) {
        this.zirkId = zirkId;
    }

}
